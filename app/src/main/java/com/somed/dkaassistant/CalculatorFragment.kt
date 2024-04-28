package com.somed.dkaassistant

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.somed.dkaassistant.databinding.FragmentCalculatorBinding


open class CalculatorFragment : Fragment() {

    //region Variables Declaration

    private lateinit var binding: FragmentCalculatorBinding

    //region Variables For Sliding Open Acid Base Calculator Panel
    private var isAcidBaseCalculatorShown = false
    private var defaultCalculatorY = 0f // Default y position of calculator panel
    private var currentCalculatorY = 0f // Current y position of calculator panel
    private var deltaYCalculator = 0f // Delta y position of calculator panel
    private var firstDownTouchY = 0f // Reference First Touch Point When User Starts to Swipe
    private var movedDistance = 0f // Slided Distance
    private var cancellingReset = false
    private var trackingDeltaY = 0f
    private var trackingY = 0f
    private var alreadySymbolRotatedLeft = false
    private var alreadySymbolRotatedRight = false
    private var lastAttemptToSwipe = ""
    //endregion

    //region Patient Input Data
    private var age = 0
    private var weight = 0.0
    private var rbs = 0
    private var isRBSMg = true

    private var phosphorus = 0.0
    private var isPhosphorusMg = false
    private var potassium = 0.0
    private var magnseium = 0.0
    private var isMagnseiumMg = false

    private var sodium = 0
    private var chloride = 0
    private var albumin = 0.0
    private var isAlbuminGpLiter = false

    private var pH = 0.0
    private var pCO2 = 0.0
    private var cHCO3 = 0.0

    private var adolescentDehydrationPercentage = 0
    //endregion

    //endregion

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calculator, container, false)

        setAppDynamicAnimations()


        binding.ageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iAge = s.toString()

                if (iAge == "") {
                    age = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                } else if (iAge.toInt() in 14..120) {
                    age = iAge.toInt()
                    getInfusions()
                } else {
                    age = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.weightInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iWeight = s.toString()

                if (iWeight == "") {
                    weight = 0.0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                } else if (iWeight.toDouble() in 20.0..300.0) {
                    weight = iWeight.toDouble()
                    getInfusions()
                } else {
                    weight = 0.0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.rbsInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iRBS = s.toString()

                if (iRBS == "") {
                    rbs = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                } else if (iRBS.toInt() in 1..1200) {
                    rbs = iRBS.toInt()
                    getInfusions()
                } else {
                    rbs = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.potInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iPotassium = s.toString()

                if (iPotassium == "") {
                    potassium = 0.0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                } else if (iPotassium.toDouble() in 0.1..15.0) {
                    potassium = iPotassium.toDouble()
                    getInfusions()
                } else {
                    potassium = 0.0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.sodInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iSodium = s.toString()

                if (iSodium == "") {
                    sodium = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                } else if (iSodium.toDouble() in 95.0..200.0) {
                    sodium = iSodium.toInt()
                    getCorrectedSodium()
                    getInfusions()
                } else {
                    sodium = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.po4Input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val iPhosphorus = s.toString()

                if (iPhosphorus == "") {
                    phosphorus = 0.0
                } else if (iPhosphorus.toDouble() in 0.1..5.0) {
                    phosphorus = iPhosphorus.toDouble()
                    getInfusions()
                } else {
                    phosphorus = 0.0
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        setSwipeDelete(binding.ageInput)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getInfusions() {

        if (age != 0 && weight != 0.0 && rbs != 0 && potassium != 0.0 && sodium != 0) {
            //showToast("getting infusion")
            if (potassium < 3.3) {
                binding.tvInsulin.text = "Hold Insulin Infusion"
            } else {
                when (rbs) {
                    in 201..1200 -> binding.tvInsulin.text =
                        "Insulin Rate: ${"%.1f".format(weight * 0.1)} mL/h\n(0.1u/kg/h)"

                    in 175..200 -> binding.tvInsulin.text =
                        "Insulin Rate: ${"%.1f".format(weight * 0.05)} mL/h\n(0.05u/kg/h)"

                    in 150..174 -> binding.tvInsulin.text =
                        "Insulin Rate: ${"%.1f".format(weight * 0.04)} mL/h\n(0.04u/kg/h)"

                    in 125..149 -> binding.tvInsulin.text =
                        "Insulin Rate: ${"%.1f".format(weight * 0.03)} mL/h\n(0.03u/kg/h)"

                    in 70..124 -> binding.tvInsulin.text =
                        "Insulin Rate: ${"%.1f".format(weight * 0.02)} mL/h\n(0.02u/kg/h)"

                    in 1..69 -> binding.tvInsulin.text = "Hold Insulin Infusion"
                }
            }

            if (age >= 18) {// Adult more than 18 years old

            } else {// Adult less that 18 years old

                val typeOfSaline = when {
                    sodium < 130 && potassium < 5.3 -> "0.9% NaCl + KCL 20 mEq amp"
                    sodium >= 130 && potassium < 5.3 -> "0.45% NaCl + KCL 20 mEq amp"
                    sodium < 130 && potassium >= 5.3 -> "0.9% NaCl"
                    sodium >= 135 && potassium >= 5.3 -> "0.45% NaCl"
                    else -> ""
                }

                val typeOfDextrose = when {
                    phosphorus == 0.0 -> "D₁₀W"
                    phosphorus < 1.12 && potassium < 5.3 -> "D₁₀W + Potassium Phosphate ${
                        (getPhosDose(
                            phosphorus
                        ) / 3)
                    } mL"

                    phosphorus < 1.12 && potassium >= 5.3 -> "D₁₀W + Sodium Glycerophosphate ${
                        getPhosDose(
                            phosphorus
                        )
                    } mL"

                    phosphorus >= 1.12 -> "D₁₀W"
                    else -> ""
                }

                if (potassium >= 3.3) {
                    when (rbs) {
                        in 300..1200 -> binding.tvIVInfusion.text =
                            "$typeOfSaline @ ${approximateRate(getInfusionFluid())} mL/h"

                        in 250..299 -> binding.tvIVInfusion.text =
                            "$typeOfSaline @ ${approximateRate(getInfusionFluid() * 0.75)} mL/h + \n $typeOfDextrose @ ${
                                approximateRate(getInfusionFluid() * .25)
                            } mL/h"

                        in 200..249 -> binding.tvIVInfusion.text =
                            "$typeOfSaline @ ${approximateRate(getInfusionFluid() * 0.5)} mL/h + \n $typeOfDextrose @ ${
                                approximateRate(getInfusionFluid() * 0.5)
                            } mL/h"

                        in 150..199 -> binding.tvIVInfusion.text =
                            "$typeOfSaline @ ${approximateRate(getInfusionFluid() * 0.25)} mL/h + \n $typeOfDextrose @ ${
                                approximateRate(getInfusionFluid() * 0.75)
                            } mL/h"

                        in 1..149 -> binding.tvIVInfusion.text =
                            "$typeOfDextrose @ ${approximateRate(getInfusionFluid())} mL/h"
                    }
                } else {
                    when (sodium) {
                        in 131..190 -> binding.tvIVInfusion.text =
                            "KCl 10 mEq + 200 mL 0.45% NaCl over 1 hour for 3 hours"

                        in 1..130 -> binding.tvIVInfusion.text =
                            "KCl 10 mEq + 200 mL 0.9% NaCl over 1 hour for 3 hours"
                    }
                }

                binding.resusNote.text =
                    "Note:\nIf patient is NOW in Resuscitation Phase: (0-60 minute)\n\nIf Shocked (HR, capillary refill time):\nGive Patient ${(20 * weight).toInt()} mL of 0.9% NaCl over 15 minutes\nIf required, give further ${(10 * weight).toInt()} mL boluses up to a max. 4 times\nat which point inotropes should be considered\nThis bolus should NOT be subtracted from the fluid maintenance\n\nIf Non-shocked patients:\n${(10 * weight).toInt()} mL bolus of 0.9% NaCl over 60 minutes\nThese boluses SHOULD be subtracted from the fluid maintenance"
            }
        }
    }

    private fun approximateRate(number: Double): Int {
        val approximateNumber = number - (number % 10) + if (number % 10 >= 5) 10 else 0
        return approximateNumber.toInt()
    }

    private fun getPhosDose(phos: Double): Int {
        val dose = when (phos) {
            in 0.43..1.11 -> 0.2 * weight
            in 0.36..0.42 -> 0.3 * weight
            in 0.1..0.35 -> 0.4 * weight
            else -> 0.0
        }
        // showToast(dose.toString())
        return dose.toInt()
    }

    private fun getCorrectedSodium() {
        if (rbs > 100) {
            sodium = (sodium + (0.020 * (rbs - 100))).toInt()
        }
    }

    private fun getDehydrationPercentage(): Int {
        adolescentDehydrationPercentage = when {
            pH in 7.2..7.29 || cHCO3 in 10.0..14.9 -> 5
            pH in 7.1..7.19 || cHCO3 in 5.0..9.9 -> 7
            pH in 6.8..7.09 || cHCO3 in 0.0..4.9 -> 10
            else -> 0 // default value if none of the conditions are met
        }

        return adolescentDehydrationPercentage
    }

    // Function to calculate fluid deficit based on dehydration level and weight
    private fun calculateFluidDeficit(): Double {
        return (getDehydrationPercentage() * weight * 10) - (weight * 10)
    }

    // Function to calculate maintenance fluid based on body weight using the Holliday-Segar formula return mL/day
    private fun calculateMaintenanceFluid(): Double {
        val maintenanceFluid = when {
            weight <= 10 -> 100 * weight
            weight <= 20 -> 1000 + (weight - 10) * 50
            else -> 1500 + (weight - 20) * 20
        }
        return maintenanceFluid
    }

    // Function to calculate hourly fluid rate
    private fun getInfusionFluid(): Double {
        return (calculateFluidDeficit() / 48) + (calculateMaintenanceFluid() / 24)
    }

    fun iToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getInfusionFluid(weight: Double): Int {

        var fluidRequirement: Double = if (weight <= 10.0) {
            weight * 4 // 4 mL/kg/hour for the first 10 kg
        } else if (weight <= 20.0) {
            40 + (weight - 10) * 2 // 40 mL/hour for the first 10 kg, plus 2 mL/kg/hour for every kg above 10 kg
        } else {
            60 + (weight - 20) * 1 // 60 mL/hour for the first 20 kg, plus 1 mL/kg/hour for every kg above 20 kg
        }

// Limit the fluid requirement to a maximum of 100 mL/hour
        fluidRequirement = fluidRequirement.coerceAtMost(100.0)

        return approximateRate(fluidRequirement)
    }

    @SuppressLint("ClickableViewAccessibility")
    protected fun setSwipeDelete(editText: EditText) {
        editText.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                // Toast.makeText(this@MainActivity, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show()

                editText.text = null
                editText.requestFocus()
                Toast.makeText(requireContext(), "Swipe left gesture detected", Toast.LENGTH_SHORT)
                    .show()

            }


            /*
            override fun onClick() {
                 super.onClick()
                 heightET.requestFocus()
             }

            override fun onSwipeRight() {
                 super.onSwipeRight()
                 // Toast.makeText(this@MainActivity,"Swipe Right gesture detected", Toast.LENGTH_SHORT).show()
             }

             override fun onSwipeUp() {
                 super.onSwipeUp()
                // Toast.makeText(this@MainActivity, "Swipe up gesture detected", Toast.LENGTH_SHORT).show()
             }

            */
            override fun onSwipeDown() {
                super.onSwipeDown()
                Toast.makeText(requireContext(), "Swipe down gesture detected", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun approximateRateBak(number: Int): Int {
        val remainder = number % 10
        val approximateNumber = if (remainder < 5) {
            number - remainder // Round down to the nearest multiple
        } else {
            number - remainder + 10 // Round up to the nearest multiple
        }

        return approximateNumber
    }


    private fun setAppDynamicAnimations() {

        //region Tapping On Chevron and Textview To Open Animate Acid Base Calculator Section
        binding.chevAcidBase.setOnClickListener {
            tapOpenAcidBaseCalculator()
        }

        binding.tvAcidBaseCalculatorTitle.setOnClickListener {
            tapOpenAcidBaseCalculator()
        }
        //endregion

        //region Clear Any Focus from Text Input Fields, If User Press Back Button to Close Keyboard
        clearFocusOfTextInput()
        //endregion

        //region Set Swipe Refresh For Calculator Panel
        // Wait until layout get inflated and get the default Y of Calculator Panel
        getCalculatorPanelY()

        // Respond to Swipe Calculator Panel Down to Refresh
        setSwipeRefreshAnimation()
        //endregion
    }

    private fun tapOpenAcidBaseCalculator() {
        if (!isAcidBaseCalculatorShown) {
            // Rotate 180 degrees clockwise
            ObjectAnimator.ofFloat(binding.chevAcidBase, "rotation", 0f, 180f)
                .start()
            binding.lnAGAcidBaseCalculator.visibility = View.VISIBLE
            binding.potInput.imeOptions = EditorInfo.IME_ACTION_DONE
        } else {
            // Rotate 180 degrees counterclockwise
            ObjectAnimator.ofFloat(binding.chevAcidBase, "rotation", 180f, 0f)
                .start()
            binding.lnAGAcidBaseCalculator.visibility = View.GONE
            binding.potInput.imeOptions = EditorInfo.IME_ACTION_NEXT
        }
        isAcidBaseCalculatorShown = !isAcidBaseCalculatorShown
    }

    private fun clearFocusOfTextInput() {
        val decorView = activity?.window?.decorView
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            var alreadyOpen = false
            val defaultKeyboardHeightDP = 100
            val EstimatedKeyboardDP = defaultKeyboardHeightDP + 48
            val rect = Rect()

            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(rect)
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    decorView.resources.displayMetrics
                ).toInt()
                val heightDiff = decorView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight

                if (isShown == alreadyOpen) {
                    return
                }
                alreadyOpen = isShown
                if (!isShown) {
                    // Do something when keyboard is opened
                    binding.ageInput.clearFocus()
                    binding.weightInput.clearFocus()
                    binding.rbsInput.clearFocus()
                    binding.po4Input.clearFocus()
                    binding.mgInput.clearFocus()
                    binding.potInput.clearFocus()
                    binding.sodInput.clearFocus()
                    binding.chlorideInput.clearFocus()
                    binding.albuminInput.clearFocus()
                    binding.pHInput.clearFocus()
                    binding.pCO2Input.clearFocus()
                    binding.HCO3Input.clearFocus()
                }
            }
        })
    }

    private fun getCalculatorPanelY() {
        binding.lnCalculator.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Ensure we call this only once
                binding.lnCalculator.viewTreeObserver.removeOnGlobalLayoutListener(this)
                defaultCalculatorY = binding.lnCalculator.y
                // Now you can get the dimensions or perform any other operations on linearLayout
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSwipeRefreshAnimation() {
        binding.lnCalculator.setOnTouchListener { _, sEvent ->

            currentCalculatorY = binding.lnCalculator.y

            when (sEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Toast.makeText(requireContext(), "You touched the screen", Toast.LENGTH_SHORT).show()
                    // Store the Intial touch point (Y position) as the user first touch the screen
                    firstDownTouchY = sEvent.rawY
                    trackingY = firstDownTouchY
                    trackingDeltaY = sEvent.rawY - trackingY
                    // Distance the panel has to move from default Y position to user current finger position
                    deltaYCalculator = defaultCalculatorY - sEvent.rawY
                }

                MotionEvent.ACTION_UP -> {
                    //Toast.makeText(requireContext(), "You lift your finger off screen", Toast.LENGTH_SHORT).show()
                    // Return the LinearLayout to its original position when the user lifts his finger

                    binding.lnCalculator.animate()
                        .y(defaultCalculatorY)
                        .setDuration(0)
                        .start() // Return to original position

                    binding.tvReset.visibility = View.INVISIBLE

                    if (movedDistance > 75 && !cancellingReset) {
                        // If user move reaches the target distance of 75f, reset the input field
                        binding.ageInput.text = null
                        binding.weightInput.text = null
                        binding.rbsInput.text = null
                        binding.po4Input.text = null
                        binding.mgInput.text = null
                        binding.potInput.text = null
                        binding.sodInput.text = null
                        binding.chlorideInput.text = null
                        binding.albuminInput.text = null
                        binding.pHInput.text = null
                        binding.pCO2Input.text = null
                        binding.HCO3Input.text = null

                        // Put the cursor inside the first field, the Age Text Input Field
                        binding.ageInput.requestFocus()

                        // Force Display of Keyboard
                        val inputMethodManager =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        val view = activity?.currentFocus
                        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

                        // Reset Our Calculation
                        movedDistance = 0f
                        alreadySymbolRotatedLeft = false
                        alreadySymbolRotatedRight = false
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    // Toast.makeText(requireContext(), "You are keeping your finger on screen", Toast.LENGTH_SHORT).show()

                    movedDistance = currentCalculatorY - defaultCalculatorY

                    // Prevents the user moving the panel beyond its original position

                    if (trackingDeltaY > sEvent.rawY - trackingY) {
                        /*if (lastAttempttoSwipe == "Down") {
                            alreadySymobolRotatedRight = false
                        }*/
                        lastAttemptToSwipe = "Up"
                        // iToast("You are Swiping Up")
                        cancellingReset = true

                        /*if (!alreadySymobolRotatedRight) {
                            val rotateRight = RotateAnimation(
                                270f,
                                0f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                            )
                            rotateRight.duration = 500
                            rotateRight.interpolator = LinearInterpolator()

                            binding.tvRefreshSymbol.startAnimation(rotateRight)

                            alreadySymobolRotatedRight = true
                        }*/
                    } else if (trackingDeltaY < sEvent.rawY - trackingY) {
                        /* if (lastAttempttoSwipe == "Up") {
                             alreadySymobolRotatedLeft = false
                         }
                         lastAttempttoSwipe = "Down"*/
                        // iToast("You are Swiping Down")
                        cancellingReset = false

                        if (!alreadySymbolRotatedLeft) {
                            val rotateLeft = RotateAnimation(
                                270f,
                                0f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                            )
                            rotateLeft.duration = 500
                            rotateLeft.interpolator = LinearInterpolator()

                            binding.tvRefreshSymbol.startAnimation(rotateLeft)
                            alreadySymbolRotatedLeft = true
                        }
                    }

                    trackingY = sEvent.rawY
                    trackingDeltaY = sEvent.rawY - trackingY

                    if ((currentCalculatorY >= defaultCalculatorY && movedDistance < 175f)||(cancellingReset && currentCalculatorY >= defaultCalculatorY)) {
                        // Make Reset Instruction Visible behind the panel
                        binding.tvReset.visibility = View.VISIBLE

                            // Move the Calculator Panel as user moves his finger downwards or upwards
                            binding.lnCalculator.animate()
                                .y(sEvent.rawY + deltaYCalculator)
                                .setDuration(0)
                                .start()
                    }
                }
            }
            true
        }
    }




}
