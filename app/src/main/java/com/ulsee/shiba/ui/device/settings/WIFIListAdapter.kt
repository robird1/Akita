package com.ulsee.shiba.ui.device.settings

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.shiba.R
import com.ulsee.shiba.model.WIFIInfo

class WIFIListAdapter(private val activity: WIFIListActivity)  : RecyclerView.Adapter<WIFIListAdapter.ViewHolder>() {

    private var wifiList : List<WIFIInfo> = ArrayList()
    fun setList(list: List<WIFIInfo>) {
        wifiList = list
        notifyDataSetChanged()
    }
    fun getList():List<WIFIInfo> {
        return wifiList
    }

    override fun getItemCount(): Int = this.wifiList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(wifiList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_list_wifi, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val nameTV = itemView?.findViewById<TextView>(R.id.textView_ssid)
        private val barTV = itemView?.findViewById<TextView>(R.id.textView_bar)
        var wifi : WIFIInfo? = null

        init {
            itemView.setOnClickListener {
                val wifiInfo = wifiList[absoluteAdapterPosition]
                Log.d("WIFIListActivity", "wifiInfo.ssid: "+ wifiInfo.ssid+ " wifiInfo.bssid: "+
                        wifiInfo.bssid+ " wifiInfo.password: "+ wifiInfo.password+ " wifiInfo.security: "+ wifiInfo.security)
//                if(!wifiInfo.passwordRequired) {
//                    activity.connectToWIFI(wifiInfo, null)
//                } else {
//                    activity.askPassword(wifiInfo)
//                }
                activity.setActivityResult(wifiInfo)
            }
        }

        fun bind(wifi: WIFIInfo) {
            this.wifi = wifi
            nameTV?.text = wifi.ssid
            barTV?.text = wifi.bars
        }
    }
}