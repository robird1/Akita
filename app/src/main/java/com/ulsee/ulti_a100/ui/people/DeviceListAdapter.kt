package com.ulsee.ulti_a100.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.ui.record.DeviceListAdapter
import com.ulsee.ulti_a100.ui.record.DeviceListViewModel
import com.ulsee.ulti_a100.ui.record.RecordViewHolder

class DeviceListAdapter(private val viewModel: DeviceListViewModel): DeviceListAdapter(viewModel) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_record_device, parent, false)
        return PeopleDeviceViewHolder(view, viewModel)
    }
}

class PeopleDeviceViewHolder(itemView: View, private val viewModel: DeviceListViewModel) : RecordViewHolder(itemView, viewModel) {
    override fun getNavigateAction() = DeviceFragmentDirections.actionToPeople(device!!.getIP())

}
