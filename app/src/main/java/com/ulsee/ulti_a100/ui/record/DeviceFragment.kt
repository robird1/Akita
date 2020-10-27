package com.ulsee.ulti_a100.ui.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.databinding.FragmentRecordDeviceListBinding
import com.ulsee.ulti_a100.utils.RecyclerViewItemClickSupport

private val TAG = DeviceFragment::class.java.simpleName

class DeviceFragment : Fragment() {
    private lateinit var binding: FragmentRecordDeviceListBinding
    private lateinit var viewModel: DeviceListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordDeviceListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, DeviceListFactory(DeviceInfoRepository()))
            .get(DeviceListViewModel::class.java)

        observeDeviceList()

        binding.recyclerView.adapter = DeviceListAdapter(viewModel, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val support: RecyclerViewItemClickSupport = RecyclerViewItemClickSupport.addTo(binding.recyclerView)
        support.setOnItemClickListener { recyclerView, position, _ ->
            val device = (recyclerView.adapter as DeviceListAdapter).getList()[position]
            val action = DeviceFragmentDirections.actionToAttendRecord(device.getIP())
            findNavController().navigate(action)
        }

        (activity as MainActivity).setTitle("Records")

        return binding.root
    }

    override fun onDestroyView() {
        Log.d(TAG, "[Enter] onDestroyView")
        super.onDestroyView()
        viewModel.cancelAllConnectionJobs()
    }

    private fun observeDeviceList() {
        viewModel.deviceList.observe(viewLifecycleOwner, {
            Log.d(TAG, "[Enter] observeDeviceList")
            (binding.recyclerView.adapter as DeviceListAdapter).setList(it)
        })
    }

}