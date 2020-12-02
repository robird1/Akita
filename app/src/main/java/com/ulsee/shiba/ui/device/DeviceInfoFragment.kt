package com.ulsee.shiba.ui.device

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.databinding.FragmentDeviceInfoBinding

private val TAG = DeviceInfoFragment::class.java.simpleName

class DeviceInfoFragment : Fragment() {
    private lateinit var binding: FragmentDeviceInfoBinding
    private lateinit var viewModel: DeviceInfoViewModel
    private val args: DeviceInfoFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "[Enter] onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "[Enter] onCreate")
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceInfoBinding.inflate(inflater, container, false)
        val deviceID = args.recordID
//        Log.d(TAG, "args.recordID: $deviceID")

        viewModel = ViewModelProvider(this, DeviceInfoFactory(DeviceInfoRepository(), deviceID))
            .get(DeviceInfoViewModel::class.java)
        viewModel.deviceInfo.observe(viewLifecycleOwner, {
            binding.textViewSn.text = it.getSN()
            binding.textViewMac.text = it.getMAC()
            binding.textViewCid.text = it.getChipID()
            binding.textViewIp.text = it.getIP()
            binding.textViewName.text = it.getID()
        })

        (activity as MainActivity).setTitle("Device Information")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "[Enter] onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "[Enter] onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "[Enter] onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "[Enter] onDestroyView")
    }

    override fun onDestroy() {
        Log.d(TAG, "[Enter] onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "[Enter] onDetach")
    }

}