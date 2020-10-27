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
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.databinding.FragmentLightModeBinding
import kotlinx.android.parcel.Parcelize

class LightModeFragment: Fragment() {
    private lateinit var binding: FragmentLightModeBinding
    private lateinit var viewModel: LightModeViewModel
    private val args: LightModeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLightModeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, LightModeFactory(SettingRepository(args.lightMode.url)))
            .get(LightModeViewModel::class.java)

        initValues()
        observeSetDeviceConfig()

        binding.applyButton.setOnClickListener {
            binding.progressView.visibility = View.VISIBLE
            viewModel.setDeviceConfig(getInput())
        }

        (activity as MainActivity).setTitle("Mode")

        return binding.root
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = LightModeFragmentDirections.actionToDeviceSettings(args.lightMode.url)
            findNavController().navigate(action)
        })
    }

    private fun initValues() {
        binding.chipGroupLight.check(when(args.lightMode.lightMode) {
            0 -> R.id.light_on
            1 -> R.id.light_off
            else -> R.id.light_auto
        })
        binding.chipGroupLcd.check(when(args.lightMode.lcdMode) {
            0 -> R.id.lcd_on
            1 -> R.id.lcd_off
            else -> R.id.lcd_auto
        })

    }

    private fun getInput(): LightModeData {
        val lightValue = when(binding.chipGroupLight.checkedChipId) {
            R.id.light_on -> 0
            R.id.light_off -> 1
            else -> 2
        }
        val lcdValue = when(binding.chipGroupLcd.checkedChipId) {
            R.id.lcd_on -> 0
            R.id.lcd_off -> 1
            else -> 2
        }
        return LightModeData(lightValue, lcdValue, args.lightMode.url)
    }

}


@Parcelize
data class LightModeData(var lightMode: Int, var lcdMode: Int, var url: String): Parcelable
