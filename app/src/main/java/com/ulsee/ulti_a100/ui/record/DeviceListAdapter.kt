package com.ulsee.ulti_a100.ui.record

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.model.Device

private val TAG = DeviceListAdapter::class.java.simpleName

class DeviceListAdapter(private val viewModel: DeviceListViewModel, private val fragment: DeviceFragment) : RecyclerView.Adapter<RecordViewHolder>() {

    private var deviceList: List<Device> = ArrayList()
    fun setList(list: List<Device>) {
        deviceList = list
        notifyDataSetChanged()
    }

    fun getList() = deviceList

    override fun getItemCount(): Int = this.deviceList.size

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        Log.d(TAG, "[Enter] onBindViewHolder position: $position")
        holder.bind(deviceList[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        Log.d(TAG, "[Enter] onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_record_device, parent, false)
        return RecordViewHolder(view, viewModel, fragment)
    }
}


class RecordViewHolder(itemView: View, private val viewModel: DeviceListViewModel, private val fragment: DeviceFragment) : RecyclerView.ViewHolder(itemView) {
    private val nameTV = itemView.findViewById<TextView>(R.id.device_name)
    private val ipTV = itemView.findViewById<TextView>(R.id.device_ip)
    private val connectedView = itemView.findViewById<View>(R.id.view_connected)
    private val notConnectedView = itemView.findViewById<View>(R.id.view_not_connected)
    var device: Device? = null
    private var deviceID = ""

    fun bind(device: Device, position: Int) {
        this.device = device
        deviceID = device.getID()
        nameTV?.text = device.getID()
        ipTV?.text = device.getIP()

        viewModel.getConnectionStatus(device.getIP(), position, this)
    }

    fun displayConnectionStatus(isConnected: Boolean) {
        connectedView.visibility = if (isConnected) View.VISIBLE else View.GONE
        notConnectedView.visibility = if (!isConnected) View.VISIBLE else View.GONE
    }

}
