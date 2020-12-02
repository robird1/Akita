package com.ulsee.ulti_a100.ui.device.settings

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
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.data.response.Data3
import com.ulsee.ulti_a100.databinding.FragmentTimeSyncBinding
import kotlinx.android.synthetic.main.fragment_time_sync.*
import java.text.SimpleDateFormat

private val TAG = TimeSyncFragment::class.java.simpleName

class TimeSyncFragment: Fragment() {
    private lateinit var binding: FragmentTimeSyncBinding
    private lateinit var viewModel: TimeSyncViewModel
    private val args: TimeSyncFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeSyncBinding.inflate(inflater, container, false)
        initViewModel()
        initFooterBtnListener()
        observeDeviceTime()
        observeMobileTime()
        observeSyncTime()
        (activity as MainActivity).setTitle("Time Sync")
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, TimeSyncFactory(SettingRepository(args.url)))
            .get(TimeSyncViewModel::class.java)
    }

    private fun initFooterBtnListener() {
        binding.syncBtn.isEnabled = false
        binding.syncBtn.setOnClickListener {
            binding.progressView.isVisible = true
            viewModel.syncTime()
        }
    }

    private fun observeDeviceTime() {
        viewModel.deviceTime.observe(viewLifecycleOwner, { time ->
            binding.progressView.isVisible = false
            if (time != null) {
                sync_btn.isEnabled = true
//                binding.deviceTime.setText(getDisplayTime(time))
                binding.deviceTime.setText(getDisplayTime(time))
            } else {
                sync_btn.isEnabled = false
                Toast.makeText(requireContext(), "Obtain device time failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeMobileTime() {
        viewModel.mobileTime.observe(viewLifecycleOwner, { time ->
            binding.mobileTime.setText(getDisplayTime(time))
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

    private fun getDisplayTime(time: Data3) = String.format(
        "%4d-%02d-%02d %02d:%02d:%02d",
        time.year, time.month, time.day, time.hour, time.min, time.sec
    )

    private fun getDisplayTime(time: Long) = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time)

}
