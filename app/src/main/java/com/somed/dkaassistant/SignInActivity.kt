@file:Suppress("DEPRECATION")

package com.somed.dkaassistant

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Intent
import android.os.Build
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.somed.dkaassistant.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    /*
    In the context of Google Sign-In, RC_SIGN_IN is typically used as the request code
    you pass to startActivityForResult() when you launch the sign-in intent.

    When the sign-in activity finishes, the system calls your activity’s onActivityResult() method
    with this request code, allowing you to identify the result as coming from the sign-in request
     */
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(this, R.color.status)
        }




        /* auth is an instance of FirebaseAuth, a class that contains
         all methods we need to authenticate the user.
         such as signInWithEmailAndPassword(email, password) to sign in a user, createUserWithEmailAndPassword(email, password) to create a new user,
         and getCurrentUser() to get the user who is currently signed in.
        */
        auth = FirebaseAuth.getInstance()

        /*
        auth.currentUser returns the user who is currently signed in,
        or null if no user is signed in.
        This information is stored locally on the device and is updated when the user signs in or out.
        auth.currentUser will return their user information, even if the device is offline.
        */
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }


        binding.ivLogo.postDelayed({
            val logoIntroBounceAnimatorX =
                ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 0.25f, 0.27f, 0.25f)
            logoIntroBounceAnimatorX.duration = 350
            logoIntroBounceAnimatorX.interpolator = AccelerateDecelerateInterpolator()

            val logoIntroBounceAnimatorY =
                ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 0.25f, 0.27f, 0.25f)
            logoIntroBounceAnimatorY.duration = 350
            logoIntroBounceAnimatorY.interpolator = AccelerateDecelerateInterpolator()

            val logoUpwardMoveAnimator = ObjectAnimator.ofFloat(
                binding.ivLogo,
                "translationY",
                0f,
                -binding.lnLogin.height.toFloat() * 1f
            )
            logoUpwardMoveAnimator.duration = 500
            logoUpwardMoveAnimator.interpolator = AccelerateDecelerateInterpolator()
            logoUpwardMoveAnimator.startDelay = 750
            val logoAnimationSet = AnimatorSet()

            // Create an animation to fade in the login layout.
            val loginFadeInAnimator = ObjectAnimator.ofFloat(binding.lnLogin, "alpha", 0f, 1f)
            loginFadeInAnimator.duration = 350
            loginFadeInAnimator.interpolator = AccelerateDecelerateInterpolator()


            logoAnimationSet.playTogether(
                logoIntroBounceAnimatorX,
                logoIntroBounceAnimatorY
            ) // play the bounce animations at the same time

                logoAnimationSet.play(logoUpwardMoveAnimator)
                    .after(logoIntroBounceAnimatorX) // play the move animation after the bounce animations
                // Play the fade-in animation after the move animation.

            if (auth.currentUser == null) {
                logoAnimationSet.play(logoUpwardMoveAnimator)
                    .after(logoIntroBounceAnimatorX) // play the move animation after the bounce animations
                // Play the fade-in animation after the move animation.
                logoAnimationSet.play(loginFadeInAnimator).after(logoUpwardMoveAnimator)
            }


            logoAnimationSet.start()
        }, 500)
    }

    // Show Google Account Bottom Sheet to Sign in
    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // default_web_client_id value in strings.xml, and value we get from google-service.json download from firebase website and put in app folder
            .requestEmail()
            .build() // .build(): This builds the GoogleSignInOptions object with the requested parameters.

        val googleSignInClient = GoogleSignIn.getClient(this, gso) // GoogleSignInClient object interact with the Google Sign-In API.
        val signInIntent = googleSignInClient.signInIntent // Intent display bottom sheet for Google Sign-In process.
        startActivityForResult(signInIntent, RC_SIGN_IN) // When the signing in attempt is completed, the result will be returned to your Activity onActivityResult method. RC_SIGN_IN is a request code that you define to identify the result.
    }

    /*
    Authenticating with Google Credentials
    Now we need to handle the sign-in result in onActivityResult.
    To do this, you’ll need to override the onActivityResult method in your activity as follows:
    */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) // This gets a Task object from the sign-in intent. A Task is a Google Play Services class that represents an asynchronous operation.
            try {
                // Google Sign In was successful, authenticate with Firebase
                // This attempts to get the GoogleSignInAccount object from the Task. If the sign-in was successful, this will contain the user’s account information. If the sign-in failed, this will throw an ApiException.
                val account = task.getResult(ApiException::class.java)
                // firebaseAuthWithGoogle(account.idToken) is the function that we will us to authenticate the user with Google, using Firebase.
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    firebaseAuthWithGoogle(account.idToken) is the function that use verified
    user account on Google, and register him on Firebase. Here is the function implementation:
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}