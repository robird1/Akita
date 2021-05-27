package com.ulsee.ulti_a100.ui.device.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ulsee.ulti_a100.model.WIFIInfo
import com.ulsee.ulti_a100.utils.NetworkUtils

private val TAG = WifiListViewModel::class.java.simpleName

class WifiListViewModel: ViewModel() {
    private var _startScanResult = MutableLiveData<Boolean>()
    val startScanResult: LiveData<Boolean>
        get() = _startScanResult
    private var _onReceiveResult = MutableLiveData<Boolean>()
    val onReceiveResult: LiveData<Boolean>
        get() = _onReceiveResult


    fun onReceive(intent: Intent) {
        _onReceiveResult.value = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
    }

    fun loadWIFIList(context: Context) {
        NetworkUtils.checkLocationSetting(context as Activity)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        _startScanResult.value = wifiManager.startScan()
    }

    fun getWifiInfo(context: Context): Pair<MutableList<ScanResult>, ArrayList<WIFIInfo>> {
        val wifiManager = context.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val results = wifiManager.scanResults
        var wifiInfoList = ArrayList<WIFIInfo>()
        Log.d(TAG, "results.size: ${results.size}")
        for (result in results) {
            if (result.BSSID == wifiManager.connectionInfo.bssid) {
                val level =
                    WifiManager.calculateSignalLevel(wifiManager.connectionInfo.rssi, result.level)
                val difference = level * 100 / result.level
                var signalStrangth = 0
                if (difference >= 100) signalStrangth =
                    4 else if (difference >= 75) signalStrangth =
                    3 else if (difference >= 50) signalStrangth =
                    2 else if (difference >= 25) signalStrangth = 1

            }
            if (result.SSID.isNullOrEmpty()) continue
            var wifiInfo = WIFIInfo()
            wifiInfo.ssid = result.SSID
            wifiInfo.bssid = result.BSSID

            val level = WifiManager.calculateSignalLevel(result.level, 5)
            when (level) {
                0 -> wifiInfo.bars = "____"
                1 -> wifiInfo.bars = "▂___"
                2 -> wifiInfo.bars = "▂▄__"
                3 -> wifiInfo.bars = "▂▄▆_"
                4 -> wifiInfo.bars = "▂▄▆█"
                else -> wifiInfo.bars = "____"
            }

            wifiInfo.capabilities = result.capabilities
            Log.d(
                TAG,
                String.format(
                    "got Wi-Fi, ssid=%s, bssid=%s, capabilities=%s ",
                    result.SSID,
                    result.BSSID,
                    result.capabilities
                )
            )
            wifiInfoList.add(wifiInfo)
            //                    Log.d("WIFIListActivity", String.format("got Wi-Fi, ssid=%s, bssid=%s, capabilities=%s security=%s passwordRequired=%s", result.SSID, result.BSSID, result.capabilities))
            //                    " wifiInfo.security: "+ wifiInfo.security+ " wifiInfo.passwordRequired: "
        }
        return Pair(results, wifiInfoList)
    }

}
