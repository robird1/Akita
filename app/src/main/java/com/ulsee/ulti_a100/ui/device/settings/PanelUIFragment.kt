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
import com.ulsee.ulti_a100.databinding.FragmentPanelUiConfigBinding
import kotlinx.android.parcel.Parcelize

private val TAG = PanelUIFragment::class.java.simpleName

class PanelUIFragment: Fragment() {
    private lateinit var binding: FragmentPanelUiConfigBinding
    private lateinit var viewModel: PanelUIViewModel
    private val args: PanelUIFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPanelUiConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, PanelUIFactory(SettingRepository(args.panelUi.url)))
            .get(PanelUIViewModel::class.java)

        initValues()
        observeSetDeviceConfig()

        binding.applyBtn.setOnClickListener {
            binding.progressView.visibility = View.VISIBLE
            viewModel.setDeviceConfig(getInput())
        }

        (activity as MainActivity).setTitle("Panel UI")

        return binding.root
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = PanelUIFragmentDirections.actionToDeviceSettings(args.panelUi.url)
            findNavController().navigate(action)
        })
    }

    private fun initValues() {
        binding.checkBoxIp.isChecked = args.panelUi.showIP
        binding.checkBoxMac.isChecked = args.panelUi.showMAC
        binding.checkBoxFrame.isChecked = args.panelUi.showFrame
        binding.checkBoxRegistration.isChecked = args.panelUi.showPeopleCount
        binding.checkBoxRecognizeArea.isChecked = args.panelUi.showArea
        binding.checkBoxRecognizeResult.isChecked = args.panelUi.showResult
        binding.checkBoxTemperature.isChecked = args.panelUi.showTemperature
    }

    private fun getInput(): PanelUIData {
        return PanelUIData(binding.checkBoxIp.isChecked, binding.checkBoxMac.isChecked,
            binding.checkBoxFrame.isChecked, binding.checkBoxRegistration.isChecked,
            binding.checkBoxRecognizeArea.isChecked, binding.checkBoxRecognizeResult.isChecked,
            binding.checkBoxTemperature.isChecked, args.panelUi.url)
    }

}


@Parcelize
data class PanelUIData(var showIP: Boolean, var showMAC: Boolean, var showFrame: Boolean,
                       var showPeopleCount: Boolean, var showArea: Boolean, var showResult: Boolean,
                       var showTemperature: Boolean, var url: String): Parcelable