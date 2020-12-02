package com.ulsee.shiba.ui.device.settings

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.R
import com.ulsee.shiba.databinding.FragmentTemperatureConfigBinding
import kotlinx.android.parcel.Parcelize

private val TAG = TemperatureFragment::class.java.simpleName

class TemperatureFragment: Fragment() {
    private lateinit var binding: FragmentTemperatureConfigBinding
    private lateinit var viewModel: TemperatureViewModel
    private var temperatureUnit = "°C"
    private val args: TemperatureFragmentArgs by navArgs()

//    enum class PickerType {
//        TEMPERATURE, OFFSET
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTemperatureConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, TemperatureFactory(SettingRepository(args.temperature.url)))
            .get(TemperatureViewModel::class.java)
        initValues()
        configUnitButton()
        configFooterButton()
        observeSetDeviceConfig()

        (activity as MainActivity).setTitle("Temperature")

        return binding.root
    }

    private fun configUnitButton() {
        binding.unitGroup.setOnCheckedChangeListener { _, checkedId ->
//            Log.d(TAG, "[Enter] checkedId: $checkedId")
            if (checkedId == R.id.radioBtn_C) {
                temperatureUnit = "°C"
                showTemperatureRange(true)
                showInputUnit("°C")
                transformValues(true)
            } else {
                temperatureUnit = "°F"
                showTemperatureRange(false)
                showInputUnit("°F")
                transformValues(false)
            }
        }
    }

    private fun transformValues(isToCelsius: Boolean) {
        var (maxInput, minInput, offsetInput) = getInput()
//        Log.d(TAG, "maxInput: $maxInput minInput: $minInput")
        if (isToCelsius) {
            maxInput = convertFahrenheitToCelcius(maxInput.toFloat()).toString()
            minInput = convertFahrenheitToCelcius(minInput.toFloat()).toString()
        } else {
            maxInput = convertCelciusToFahrenheit(maxInput.toFloat()).toString()
            minInput = convertCelciusToFahrenheit(minInput.toFloat()).toString()
        }
        binding.textInputLayoutMax.editText?.setText(String.format("%.1f", maxInput.toFloat()))
        binding.textInputLayoutMin.editText?.setText(String.format("%.1f", minInput.toFloat()))
//        binding.textInputLayoutMax.editText?.setText(maxInput)
//        binding.textInputLayoutMin.editText?.setText(minInput)
    }

    private fun convertFahrenheitToCelcius(fahrenheit: Float): Float {
        return (fahrenheit - 32) * 5 / 9
    }

    private fun convertCelciusToFahrenheit(celsius: Float): Float {
        return celsius * 9 / 5 + 32
    }

    private fun configFooterButton() {
        binding.confirmBtn.setOnClickListener {
            val isInputValid = checkInput()
            if (isInputValid) {
                binding.progressView.visibility = View.VISIBLE
                val (maxInput, minInput, offsetInput) = getInput()
                val unit = temperatureUnit.split("°")[1]
                viewModel.setDeviceConfig(maxInput, minInput, offsetInput, unit)
            } else {
                Toast.makeText(requireContext(), "Please check your input", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initValues() {
        showInputValues()
        temperatureUnit = "°${args.temperature.unit}"
        showInputUnit(temperatureUnit)

//        initTemperaturePicker()

        if (temperatureUnit == "°C") {
            selectUnitButton(R.id.radioBtn_C)
            showTemperatureRange(true)
        } else {
            selectUnitButton(R.id.radioBtn_F)
            showTemperatureRange(false)
        }
    }

    private fun selectUnitButton(resourceId: Int) {
        binding.unitGroup.check(resourceId)
    }

    private fun showInputValues() {
        binding.textInputLayoutMax.editText?.setText(String.format("%.1f", args.temperature.max))
        binding.textInputLayoutMin.editText?.setText(String.format("%.1f", args.temperature.min))
        binding.textInputLayoutOffset.editText?.setText(String.format("%.1f", args.temperature.offset))
    }

    private fun showInputUnit(unitString: String) {
        binding.textInputLayoutMax.suffixText = unitString
        binding.textInputLayoutMin.suffixText = unitString
        binding.textInputLayoutOffset.suffixText = unitString
    }

    private fun showTemperatureRange(isCelsius: Boolean) {
        if (isCelsius) {
            binding.textViewMaxRange.text = "(30.0~45.0)"
            binding.textViewMinRange.text = "(30.0~45.0)"
        } else {
            binding.textViewMaxRange.text = "(86.0~113.0)"
            binding.textViewMinRange.text = "(86.0~113.0)"
        }
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = TemperatureFragmentDirections.actionToDeviceSettings(args.temperature.url)
            findNavController().navigate(action)
        })
    }

    private fun getInput(): Triple<String, String, String> {
        val maxInput = binding.textInputLayoutMax.editText?.text.toString()
        val minInput = binding.textInputLayoutMin.editText?.text.toString()
        val offsetInput = binding.textInputLayoutOffset.editText?.text.toString()
        return Triple(maxInput, minInput, offsetInput)
    }

    private fun checkInput(): Boolean {
        val (maxInput, minInput, offsetInput) = getInput()
        if (!isFloat(maxInput) || !isFloat(minInput) || !isFloat(offsetInput))
            return false

        if (maxInput.toDouble() < minInput.toDouble())
            return false

//        if (args.temperature.unit == "C") {
        if (temperatureUnit == "°C") {
            if (maxInput.toDouble() > 45F || minInput.toDouble() < 30F) {
                return false
            }
        } else {
            if (maxInput.toDouble() > 113F || minInput.toDouble() < 86F)
                return false
        }

        if (offsetInput.toDouble() > 0.5 || offsetInput.toDouble() < -0.5)
            return false

        return true
    }

    private fun isFloat(input: String) = input.matches(Regex("[+-]?((\\d+\\.?\\d*)|(\\.\\d+))"))

//    private fun initTemperaturePicker() {
//        val editTextMin = binding.textInputLayoutMin.editText!!
//        val editTextMax = binding.textInputLayoutMax.editText!!
//        val editTextOffset = binding.textInputLayoutOffset.editText!!
//        editTextMin.setOnClickListener { showNumberPicker(editTextMin, PickerType.TEMPERATURE) }
//        editTextMax.setOnClickListener { showNumberPicker(editTextMax, PickerType.TEMPERATURE) }
//        editTextOffset.setOnClickListener { showNumberPicker(editTextOffset, PickerType.OFFSET) }
//    }
//
//    private fun showNumberPicker(editText: EditText, pickerType: PickerType) {
//        val numberPicker = NumberPicker(requireContext())
//        val valuesArray = when (pickerType) {
//            PickerType.TEMPERATURE -> {
//                if (temperatureUnit == "°C") {
//                    resources.getStringArray(R.array.temperature_range_celsius)
//                } else {
//                    resources.getStringArray(R.array.temperature_range_fahrenheit)
//                }
//            }
//            PickerType.OFFSET -> {
//                resources.getStringArray(R.array.temperature_offset)
//            }
//        }
//        numberPicker.displayedValues = valuesArray
//        numberPicker.minValue = 0
//        numberPicker.maxValue = valuesArray.size - 1
//
//        AlertDialog
//            .Builder(requireContext())
//            .setView(numberPicker)
//            .setMessage(getString(R.string.settings_picker_message))
//            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setPositiveButton(getString(R.string.ok)) { _, _ ->
//                editText.setText(valuesArray[numberPicker.value])
//            }
//            .setCancelable(true)
//            .create()
//            .show()
//    }


}


@Parcelize
data class TemperatureData(
    var unit: String,
    var max: Double,
    var min: Double,
    var offset: Double,
    var url: String
) : Parcelable