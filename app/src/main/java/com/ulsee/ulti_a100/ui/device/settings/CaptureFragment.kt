package com.ulsee.ulti_a100.ui.device.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentCaptureConfigBinding

private val TAG = CaptureFragment::class.java.simpleName

class CaptureFragment: Fragment() {
    private lateinit var binding: FragmentCaptureConfigBinding
    private lateinit var viewModel: CaptureViewModel
    private val args: CaptureFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCaptureConfigBinding.inflate(inflater, container, false)
        initViewModel()
        initCheckBoxListener()
        initIntervalListener()
        initFooterBtnListener()
        observeGetConfig()
        observeSetConfig()
        (activity as MainActivity).setTitle("Capture Attributes")
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, CaptureFactory(SettingRepository(args.url)))
            .get(CaptureViewModel::class.java)
    }


    private fun initFooterBtnListener() {
        binding.applyBtn.setOnClickListener {
            binding.progressView.isVisible = true
            viewModel.setCaptureConfigs(getUserInputs())
        }
    }

    private fun getUserInputs(): SetConfig {
        return SetConfig(binding.attributeEnable.isChecked, binding.seekBarAttribute.progress,
            binding.checkBoxTemperature.isChecked, binding.checkBoxMask.isChecked, binding.cbLogEnable.isChecked,
            binding.seekBarLog.progress, binding.strangerEnable.isChecked)
    }

    private fun initCheckBoxListener() {
        binding.attributeEnable.setOnCheckedChangeListener { _, isChecked ->
            binding.textViewInterval.isVisible = isChecked
            binding.seekBarAttribute.isVisible = isChecked
            binding.intervalValue.isVisible = isChecked
        }
        binding.cbLogEnable.setOnCheckedChangeListener { _, isChecked ->
            binding.textViewLogInterval.isVisible = isChecked
            binding.seekBarLog.isVisible = isChecked
            binding.logIntervalValue.isVisible = isChecked
        }
    }

    private fun initIntervalListener() {
        binding.seekBarAttribute.setOnSeekBarChangeListener(SeekBarListener(false))
        binding.seekBarLog.setOnSeekBarChangeListener(SeekBarListener(true))
    }

    private fun observeGetConfig() {
        viewModel.getResult.observe(viewLifecycleOwner, { configs ->
            binding.progressView.isVisible = false
            if (configs != null) {
                binding.attributeEnable.isChecked = configs.attributeRecog.toBoolean()
                binding.seekBarAttribute.progress = configs.attrInterval
                binding.checkBoxTemperature.isChecked = configs.recogBodyTemperature.toBoolean()
                binding.checkBoxMask.isChecked = configs.recogRespirator.toBoolean()
                binding.cbLogEnable.isChecked = configs.enableStoreAttendLog.toBoolean()
                binding.seekBarLog.progress = configs.attendInterval
                binding.strangerEnable.isChecked = configs.enableStoreStrangerAttLog.toBoolean()
            } else {
                Toast.makeText(requireContext(), "Obtain settings failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeSetConfig() {
        viewModel.setResult.observe(viewLifecycleOwner, { isSuccess ->
            binding.progressView.isVisible = false
            if (isSuccess) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = TimeSyncFragmentDirections.actionToDeviceSettings(args.url)
            findNavController().navigate(action)
        })
    }

    inner class SeekBarListener(private val isLog: Boolean): SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (isLog) {
                binding.logIntervalValue.text = progress.toString()
            } else {
                binding.intervalValue.text = progress.toString()
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

}
