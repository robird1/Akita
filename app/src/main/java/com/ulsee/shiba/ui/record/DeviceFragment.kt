package com.ulsee.shiba.ui.record

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.shiba.databinding.FragmentRecordDeviceListBinding
import com.ulsee.shiba.ui.device.DeviceInfoRepository

private val TAG = "RecordDeviceFragment"

class DeviceFragment : Fragment() {
    private lateinit var binding: FragmentRecordDeviceListBinding
    private lateinit var viewModel: DeviceListViewModel
//    val args: DeviceFragmentArgs by navArgs()

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
        Log.d(TAG, "[Enter] onCreateView")
//        Log.d(TAG, "type: ${args.type}")

        binding = FragmentRecordDeviceListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DeviceListFactory(DeviceInfoRepository()))
            .get(DeviceListViewModel::class.java)

        observeDeviceList()

        binding.recyclerView.adapter = DeviceListAdapter(viewModel)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

//        (activity as MainActivity).setTitle("Records")

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
        Log.d(TAG, "[Enter] onDestroyView")
        super.onDestroyView()
        viewModel.cancelAllConnectionJobs()
    }

    override fun onDestroy() {
        Log.d(TAG, "[Enter] onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "[Enter] onDetach")
    }

    private fun observeDeviceList() {
        viewModel.deviceList.observe(viewLifecycleOwner, {
            Log.d(TAG, "[Enter] observeDeviceList")
            (binding.recyclerView.adapter as DeviceListAdapter).setList(it)
        })
    }

}