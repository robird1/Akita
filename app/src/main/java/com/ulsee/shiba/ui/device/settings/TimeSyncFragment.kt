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
import com.ulsee.shiba.data.response.Data3
import com.ulsee.shiba.databinding.FragmentTimeSyncBinding
import kotlinx.android.synthetic.main.fragment_time_sync.*

private val TAG = TimeSyncFragment::class.java.simpleName

class TimeSyncFragment: Fragment() {
    private lateinit var binding: FragmentTimeSyncBinding
    private lateinit var viewModel: TimeSyncViewModel
    private val args: TimeSyncFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimeSyncBinding.inflate(inflater, container, false)
        initViewModel()
        initCheckBoxListener()
        initFooterBtnListener()
        observeDeviceTime()
        observeSyncTime()
        (activity as MainActivity).setTitle("Time Sync")
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, TimeSyncFactory(SettingRepository(args.url)))
            .get(TimeSyncViewModel::class.java)
    }

    private fun initFooterBtnListener() {
        binding.syncBtn.setOnClickListener {
            binding.progressView.isVisible = true
            viewModel.syncTime()
        }
    }

    private fun initCheckBoxListener() {
        binding.checkBoxSync.setOnCheckedChangeListener { _, isChecked ->
            sync_btn.isEnabled = isChecked
        }
    }

    private fun observeDeviceTime() {
        viewModel.deviceTime.observe(viewLifecycleOwner, { time ->
            if (time != null) {
                binding.deviceTime.setText(getDisplayTime(time))
                binding.progressView.isVisible = false
            } else {
                Toast.makeText(requireContext(), "Obtain device time failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeSyncTime() {
        viewModel.syncResult.observe(viewLifecycleOwner, { isSuccess ->
            binding.progressView.isVisible = false
            if (isSuccess) {
                Toast.makeText(requireContext(), "Sync time success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Sync time failed", Toast.LENGTH_SHORT).show()
            }
            val action = TimeSyncFragmentDirections.actionToDeviceSettings(args.url)
            findNavController().navigate(action)
        })
    }

    private fun getDisplayTime(time: Data3) = String.format("%4d-%02d-%02d %02d:%02d:%02d",
        time.year, time.month, time.day, time.hour, time.min, time.sec)

}
