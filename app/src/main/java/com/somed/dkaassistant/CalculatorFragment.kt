package com.somed.dkaassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.somed.dkaassistant.databinding.FragmentCalculatorBinding


class CalculatorFragment : Fragment() {
    private lateinit var binding: FragmentCalculatorBinding


    private var age = 0

    private var weight = 0.0

    private var rbs = 0

    private var potassium = 0.0

    private var phosphorus = 0.0

    private var pH = 0.0
    private var pCO2 = 0.0
    private var HCO3 = 0.0

    private var pHOA = 7.15
    private var HCO3OA = 8.0

    private var sodium = 0

    private var chloride = 0

    private var albumin = 0.0

    private var ketones = 0

    private var adolescentDehydrationPercentage = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calculator, container, false)

                
                binding.ageInput.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    
                    val iAge = s.toString()
                    
                    if (iAge == "") {
                    age = 0
                    binding.tvInsulin.text = ""
                    binding.tvIVInfusion.text = ""
                    }else if (iAge.toInt() in 14..120){
                    age = iAge.toInt()
                    getInfusions()
                    }else {
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
                    }else if (iWeight.toDouble() in 20.0..300.0){
                    weight = iWeight.toDouble()
                    getInfusions()
                    }else {
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
                    }else if (iRBS.toInt() in 1..1200){
                    rbs = iRBS.toInt()
                    getInfusions()
                    }else {
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
                    }else if (iPotassium.toDouble() in 0.1..15.0){
                    potassium = iPotassium.toDouble()
                    getInfusions()
                    }else {
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
                    }else if (iSodium.toDouble() in 95.0..200.0){
                    sodium = iSodium.toInt()
                    getCorrectedSodium()
                    getInfusions()
                    }else {
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
                    }else if (iPhosphorus.toDouble() in 0.1..5.0){
                    phosphorus = iPhosphorus.toDouble()
                    getInfusions()
                    }else {
                    phosphorus = 0.0
                    }
                    }
                    
                    override fun afterTextChanged(s: Editable?) {}
                    })
                

            



            return binding.root
    }


    private fun getInfusions() {

        if (age != 0 && weight != 0.0 && rbs != 0 && potassium != 0.0 && sodium != 0){
            //showToast("getting infusion")
            if (potassium < 3.3){
                binding.tvInsulin.text = "Hold Insulin Infusion"
            }else {
                when (rbs) {
                    in 201..1200 -> binding.tvInsulin.text = "Insulin Rate: ${ "%.1f".format(weight*0.1)} mL/h\n(0.1u/kg/h)"
                    in 175..200 -> binding.tvInsulin.text = "Insulin Rate: ${ "%.1f".format(weight*0.05)} mL/h\n(0.05u/kg/h)"
                    in 150..174 -> binding.tvInsulin.text = "Insulin Rate: ${ "%.1f".format(weight*0.04)} mL/h\n(0.04u/kg/h)"
                    in 125..149 -> binding.tvInsulin.text = "Insulin Rate: ${ "%.1f".format(weight*0.03)} mL/h\n(0.03u/kg/h)"
                    in 70..124 -> binding.tvInsulin.text = "Insulin Rate: ${ "%.1f".format(weight*0.02)} mL/h\n(0.02u/kg/h)"
                    in 1..69 -> binding.tvInsulin.text = "Hold Insulin Infusion"
                }
            }

            if (age >= 18){// Adult more than 18 years old

            }else{// Adult less that 18 years old

                var typeOfSaline = when {
                    sodium < 130 && potassium < 5.3 -> "0.9% NaCl + KCL 20 mEq amp"
                    sodium >= 130 && potassium < 5.3 -> "0.45% NaCl + KCL 20 mEq amp"
                    sodium < 130 && potassium >= 5.3 -> "0.9% NaCl"
                    sodium >= 135 && potassium >= 5.3 -> "0.45% NaCl"
                    else -> ""
                }

                var typeOfDextrose = when {
                    phosphorus == 0.0 -> "D₁₀W"
                    phosphorus < 1.12 && potassium < 5.3 -> "D₁₀W + Potassium Phosphate ${(getPhosDose(phosphorus)/3)} mL"
                    phosphorus < 1.12 && potassium >= 5.3 -> "D₁₀W + Sodium Glycerophosphate ${getPhosDose(phosphorus)} mL"
                    phosphorus >= 1.12 -> "D₁₀W"
                    else -> ""
                }

                if (potassium >= 3.3){
                    when (rbs) {
                        in 300..1200 -> binding.tvIVInfusion.text = "$typeOfSaline @ ${approximateRate(getInfusionFluid())} mL/h"
                        in 250..299 -> binding.tvIVInfusion.text = "$typeOfSaline @ ${approximateRate(getInfusionFluid()*0.75)} mL/h + \n $typeOfDextrose @ ${approximateRate(getInfusionFluid()*.25)} mL/h"
                        in 200..249 -> binding.tvIVInfusion.text = "$typeOfSaline @ ${approximateRate(getInfusionFluid()*0.5)} mL/h + \n $typeOfDextrose @ ${approximateRate(getInfusionFluid()*0.5)} mL/h"
                        in 150..199 -> binding.tvIVInfusion.text = "$typeOfSaline @ ${approximateRate(getInfusionFluid()*0.25)} mL/h + \n $typeOfDextrose @ ${approximateRate(getInfusionFluid()*0.75)} mL/h"
                        in 1..149 -> binding.tvIVInfusion.text = "$typeOfDextrose @ ${approximateRate(getInfusionFluid())} mL/h"
                    }
                }else{
                    when (sodium) {
                        in 131..190 -> binding.tvIVInfusion.text = "KCl 10 mEq + 200 mL 0.45% NaCl over 1 hour for 3 hours"
                        in 1..130 -> binding.tvIVInfusion.text = "KCl 10 mEq + 200 mL 0.9% NaCl over 1 hour for 3 hours"
                    }
                }

                binding.resusNote.text = "Note:\nIf patient is NOW in Resuscitative Phase: (0-60 minute)\n\nIf Shocked (HR, capillary refill time):\nGive Patinet ${(20*weight).toInt()} mL of 0.9% NaCl over 15 minutes\nIf required, give further ${(10*weight).toInt()} mL boluses up to a max. 4 times\nat which point inotropes should be considered\nThis bolus should NOT be subtracted from the fluid maintenance\n\nIf Non-shocked patients:\n${(10*weight).toInt()} mL bolus of 0.9% NaCl over 60 minutes\nThese boluses SHOULD be subtracted from the fluid maintenance"
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

    private fun getCorrectedSodium(){
        if (rbs > 100) {
            sodium = (sodium + (0.020*(rbs-100))).toInt()
        }
    }

    private fun getDehydrationPercentage():Int{
        adolescentDehydrationPercentage = when {
            pHOA in 7.2..7.29 || HCO3OA in 14.9..10.0 -> 5
            pHOA in 7.1..7.19 || HCO3OA in 9.9..5.0 -> 7
            pHOA in 6.8..7.09 || HCO3OA in 4.9..0.0 -> 10
            else -> 0 // default value if none of the conditions are met
        }

        return adolescentDehydrationPercentage
    }

    // Function to calculate fluid deficit based on dehydration level and weight
    private fun calculateFluidDeficit(): Double {
        return (getDehydrationPercentage() * weight * 10)-(weight*10)
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
        var fluidRequirement = 0.0

        fluidRequirement = if (weight <= 10.0) {
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

    /*
fun approximateRate(number: Int): Int {
val remainder = number % 10
val approximateNumber = if (remainder < 5) {
number - remainder // Round down to the nearest multiple
} else {
number - remainder + 10 // Round up to the nearest multiple
}

return approximateNumber
}
*/
}