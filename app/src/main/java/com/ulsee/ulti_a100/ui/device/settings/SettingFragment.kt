package com.ulsee.ulti_a100.ui.device.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentDeviceSettingBinding

private val TAG = SettingFragment::class.java.simpleName

class SettingFragment: Fragment() {
    private lateinit var binding: FragmentDeviceSettingBinding
    private lateinit var viewModel: SettingViewModel
    private val args: SettingFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDeviceSettingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, SettingFactory(SettingRepository(args.url)))
            .get(SettingViewModel::class.java)
//        Log.d(TAG, "args.url: ${args.url}")
        binding.recyclerView.adapter = SettingAdapter(viewModel, this, args.url)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeDeviceConfig()

        (activity as MainActivity).setTitle("Device Settings")

        return binding.root
    }

    private fun observeDeviceConfig() {
        viewModel.config.observe(viewLifecycleOwner, { data ->
            if (data != null) {
                (binding.recyclerView.adapter as SettingAdapter).setConfig(data)
            } else {
                Toast.makeText(requireContext(), "Load config error", Toast.LENGTH_LONG).show()
            }
            binding.progressView.visibility = View.INVISIBLE
        })
    }

}