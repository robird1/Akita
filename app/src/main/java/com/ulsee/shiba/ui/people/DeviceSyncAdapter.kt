package com.ulsee.shiba.ui.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.shiba.R
import com.ulsee.shiba.model.Device

private val TAG = DeviceSyncAdapter::class.java.simpleName

class DeviceSyncAdapter: RecyclerView.Adapter<DeviceSyncAdapter.ViewHolder>() {

    private val _selectedItems = hashMapOf<Int, Device>()
    val selectedItems: HashMap<Int, Device>
            get() = _selectedItems

    var deviceList: MutableList<Device> = ArrayList()
    fun setList(list: List<Device>) {
//        Log.d(TAG, "[Enter] setList")
//        peopleList = list
        deviceList.clear()
        deviceList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_sync_device_select, parent, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.device_name)
        private val ip = itemView.findViewById<TextView>(R.id.device_ip)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        private var data: Device? = null

        init {
            itemView.setOnClickListener {
                checkBox.isChecked = toggleCheckBox()
                updateSelectedItems()
            }
            checkBox.setOnClickListener {
                updateSelectedItems()
            }
        }

        private fun updateSelectedItems() {
//            Log.d(TAG, "position: $absoluteAdapterPosition isChecked: ${checkBox.isChecked}")
            if (checkBox.isChecked) {
                _selectedItems[absoluteAdapterPosition] = deviceList[absoluteAdapterPosition]
            } else {
                _selectedItems.remove(absoluteAdapterPosition)
            }
        }

        fun bind(device: Device) {
            if (device != null) {
                data = device
                name.text = device.getID()
                ip.text = device.getIP()
//                Log.d(TAG, "name: ${device.getID()} ip: ${device.getIP()}")
            }

        }

        private fun toggleCheckBox(): Boolean {
            return !checkBox.isChecked
        }

    }

}