package com.ulsee.shiba.ui.device

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.shiba.R
import com.ulsee.shiba.model.Device
import com.ulsee.shiba.ui.streaming.VlcRtspActivity

private val TAG = DeviceListAdapter::class.java.simpleName

class DeviceListAdapter(private val viewModel: DeviceListViewModel, private val fragment: DeviceFragment) : RecyclerView.Adapter<ViewHolder>() {

    private var deviceList: List<Device> = ArrayList()
    fun setList(list: List<Device>) {
        deviceList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_device, parent, false)
        return ViewHolder(view, viewModel, fragment)
    }
}


class ViewHolder(itemView: View, private val viewModel: DeviceListViewModel, private val fragment: DeviceFragment) : RecyclerView.ViewHolder(itemView) {
    private val nameTV = itemView.findViewById<TextView>(R.id.textView_deviceName)
    private val hintTV = itemView.findViewById<TextView>(R.id.textView_hint)
    private val connectedView = itemView.findViewById<View>(R.id.view_connected)
    private val notConnectedView = itemView.findViewById<View>(R.id.view_connection_status)
    private lateinit var mPopup: PopupMenu
    var device: Device? = null
    private var deviceID = ""
    private var deviceIP = ""

    init {
        initOptionMenu()
        observeConnectionStatus()

        val thumbLayout = itemView.findViewById<View>(R.id.layout_thumb)
        thumbLayout.setOnClickListener {
            showLiveStreaming(itemView)
        }
    }

    private fun initOptionMenu() {
        val menuLayout = itemView.findViewById<View>(R.id.layout_menu)
        mPopup = PopupMenu(itemView.context, menuLayout)
        mPopup.menu.add("a").setTitle("Edit Device Info")
        mPopup.menu.add("b").setTitle("Device Setting")
        mPopup.menu.add("c").setTitle("Device Info")
        mPopup.menu.add("d").setTitle("Remove")

        mPopup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.title) {
                "Edit Device Info" -> {
                    fragment.showEditDeviceDialog(device!!)
                }
                "Device Setting" -> {
                    navigateToSettingFragment(itemView)
                }
                "Device Info" -> {
                    navigateToInfoFragment()
                }
                "Remove" -> {
                    showRemoveDialog(itemView)
                }
            }

            true
        }

        menuLayout.setOnClickListener {
            Log.i(javaClass.name, "on menu layout clicked")
            mPopup.show()
        }
    }

    private fun observeConnectionStatus() {
        val lifecycleOwner = itemView.context as LifecycleOwner
        viewModel.deviceStatus.observe(lifecycleOwner, { response ->
            if (response.MAC == device?.getMAC()) {
                displayConnectionStatus(response.isConnected)
            }
        })
    }

    fun bind(device: Device) {
        this.device = device
        deviceID = device.getID()
        deviceIP = device.getIP()
        nameTV?.text = device.getID()
        viewModel.getConnectionStatus(device)
    }

    private fun displayConnectionStatus(isConnected: Boolean) {
        connectedView.visibility = if (isConnected) View.VISIBLE else View.GONE
        notConnectedView.visibility = if (!isConnected) View.VISIBLE else View.GONE
        val ctx = itemView.context
        hintTV.text =
            if (isConnected) ctx.getString(R.string.device_connected_click_to_watch_camera) else ctx.getString(
                R.string.device_not_connected_yet
            )
    }

    private fun showLiveStreaming(itemView: View) {
        if (connectedView.visibility == View.VISIBLE) {
            val uri = Uri.parse(device?.getIP())
            val url = "rtsp://${uri.host}"
            val intent = Intent(fragment.context, VlcRtspActivity::class.java)
            intent.putExtra(VlcRtspActivity.RTSP_URL, url)
            fragment.startActivity(intent)
        } else {
            Toast.makeText(itemView.context, "Device is offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSettingFragment(itemView: View) {
        if (connectedView.visibility == View.VISIBLE) {
            val action = DeviceFragmentDirections.actionToDeviceSettings(deviceIP)
            fragment.findNavController().navigate(action)
        } else {
            Toast.makeText(itemView.context, "Device is offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToInfoFragment() {
        val action = DeviceFragmentDirections.actionToDeviceInfo(deviceID)
        fragment.findNavController().navigate(action)
    }

    private fun showRemoveDialog(itemView: View) {
        AlertDialog.Builder(itemView.context)
            .setMessage(itemView.context.getString(R.string.confirm_remove_device))
            .setPositiveButton(
                itemView.context.getString(R.string.remove)
            ) { _, _ ->
                viewModel.deleteDevice(deviceID)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}

