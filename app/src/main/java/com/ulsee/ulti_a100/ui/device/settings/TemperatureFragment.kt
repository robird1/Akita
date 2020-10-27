package com.ulsee.ulti_a100.ui.device.settings

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
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentTemperatureConfigBinding
import kotlinx.android.parcel.Parcelize

private val TAG = TemperatureFragment::class.java.simpleName

class TemperatureFragment: Fragment() {
    private lateinit var binding: FragmentTemperatureConfigBinding
    private lateinit var viewModel: TemperatureViewModel
    private val args: TemperatureFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTemperatureConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, TemperatureFactory(SettingRepository(args.temperature.url)))
            .get(TemperatureViewModel::class.java)

        initValues()
        observeSetDeviceConfig()

        binding.confirmBtn.setOnClickListener {
            val isInputValid = checkInput()
            if (isInputValid) {
                binding.progressView.visibility = View.VISIBLE
                val (maxInput, minInput, offsetInput) = getInput()
                viewModel.setDeviceConfig(maxInput, minInput, offsetInput)
            } else {
                Toast.makeText(requireContext(), "Please check your input", Toast.LENGTH_SHORT).show()
            }
        }

        (activity as MainActivity).setTitle("Temperature")

        return binding.root
    }

    private fun initValues() {
        binding.textInputLayoutMax.editText?.setText(String.format("%.1f", args.temperature.max))
        binding.textInputLayoutMin.editText?.setText(String.format("%.1f", args.temperature.min))
        binding.textInputLayoutOffset.editText?.setText(String.format("%.1f", args.temperature.offset))
        val unitString = "°${args.temperature.unit}"
        binding.textInputLayoutMax.suffixText = unitString
        binding.textInputLayoutMin.suffixText = unitString
        binding.textInputLayoutOffset.suffixText = unitString
        if (unitString == "°C") {
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

        if (args.temperature.unit == "C") {
            if (maxInput.toDouble() > 45F || minInput.toDouble() < 30F) {
                return false
            }
        } else {
            if (maxInput.toDouble() > 113F || minInput.toDouble() < 86F)
                return false
        }
        return true
    }

    private fun isFloat(input: String): Boolean {
        return input.matches(Regex("[+-]?((\\d+\\.?\\d*)|(\\.\\d+))"))
    }

}


@Parcelize
data class TemperatureData(var unit: String, var max: Double, var min: Double, var offset: Double, var url: String) : Parcelable