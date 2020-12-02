package com.ulsee.ulti_a100.ui.device.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentVolumeConfigBinding

class VolumeFragment: Fragment() {
    private lateinit var binding: FragmentVolumeConfigBinding
    private lateinit var viewModel: VolumeViewModel
    private val args: VolumeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVolumeConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, VolumeFactory(SettingRepository(args.url)))
            .get(VolumeViewModel::class.java)

        initValues()
        initSeekBarListener()
        observeSetDeviceConfig()

        binding.buttonApply.setOnClickListener {
            binding.progressView.visibility = View.VISIBLE
            viewModel.setDeviceConfig(getInput())
        }

        (activity as MainActivity).setTitle("Volume")

        return binding.root
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = VolumeFragmentDirections.actionToDeviceSettings(args.url)
            findNavController().navigate(action)
        })
    }

    private fun initValues() {
        binding.seekBar.progress = args.volume
        binding.volumeValue.text = args.volume.toString()
    }

    private fun initSeekBarListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.volumeValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

    }

    private fun getInput(): Int {
        return binding.seekBar.progress
    }

}
