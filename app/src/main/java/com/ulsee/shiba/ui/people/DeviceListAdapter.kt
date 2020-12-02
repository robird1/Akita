package com.ulsee.shiba.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ulsee.shiba.R
import com.ulsee.shiba.ui.record.DeviceListAdapter
import com.ulsee.shiba.ui.record.DeviceListViewModel
import com.ulsee.shiba.ui.record.RecordViewHolder

class DeviceListAdapter(private val viewModel: DeviceListViewModel): DeviceListAdapter(viewModel) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_record_device, parent, false)
        return PeopleDeviceViewHolder(view, viewModel)
    }
}

class PeopleDeviceViewHolder(itemView: View, val viewModel: DeviceListViewModel) : RecordViewHolder(itemView, viewModel) {
    override fun getNavigateAction() = DeviceFragmentDirections.actionToPeople(device!!.getIP())

}
