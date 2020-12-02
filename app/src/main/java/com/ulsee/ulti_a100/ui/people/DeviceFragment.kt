package com.ulsee.ulti_a100.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentRecordDeviceListBinding
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.ui.record.DeviceListFactory
import com.ulsee.ulti_a100.ui.record.DeviceListViewModel

private val TAG = "PeopleDeviceFragment"

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

        binding.recyclerView.adapter = DeviceListAdapter(viewModel)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        (activity as MainActivity).setTitle("People Management")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelAllConnectionJobs()
    }

    private fun observeDeviceList() {
        viewModel.deviceList.observe(viewLifecycleOwner, {
            (binding.recyclerView.adapter as DeviceListAdapter).setList(it)
        })
    }

}