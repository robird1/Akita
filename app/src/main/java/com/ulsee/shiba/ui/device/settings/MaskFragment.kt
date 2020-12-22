package com.ulsee.shiba.ui.device.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.databinding.FragmentMaskConfigBinding

private val TAG = CaptureFragment::class.java.simpleName

class MaskFragment: Fragment() {
    private lateinit var binding: FragmentMaskConfigBinding
    private lateinit var viewModel: MaskViewModel
    private val args: MaskFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMaskConfigBinding.inflate(inflater, container, false)
        initViewModel()
        initFooterBtnListener()
        observeGetConfig()
        observeSetConfig()
        (activity as MainActivity).setTitle("Mask Attribute")
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, MaskFactory(SettingRepository(args.url)))
            .get(MaskViewModel::class.java)
    }


    private fun initFooterBtnListener() {
        binding.applyBtn.setOnClickListener {
            binding.progressView.isVisible = true
            viewModel.setConfig(binding.checkBoxMask.isChecked)
        }

    }

    private fun observeGetConfig() {
        viewModel.getResult.observe(viewLifecycleOwner, { configs ->
            binding.progressView.isVisible = false
            if (configs != null) {
                binding.checkBoxMask.isChecked = configs.recogRespirator.toBoolean()
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
            val action = MaskFragmentDirections.actionToDeviceSettings(args.url)
            findNavController().navigate(action)
        })
    }

}
