package com.ulsee.shiba.ui.device.settings

import android.app.ProgressDialog
import android.content.*
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ulsee.shiba.R
import com.ulsee.shiba.model.WIFIInfo
import com.ulsee.shiba.utils.NetworkUtils


private val TAG = WIFIListActivity::class.java.simpleName

class WIFIListActivity : AppCompatActivity() {

    val REQUEST_CODE_SWITCH_WIFI = 1234

    lateinit var recyclerView : RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var mProgressDialog : ProgressDialog
    private lateinit var mProgressView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_wifilist)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("connecting...")

        initRecyclerView()
        registerWIFIBroadcast()
        loadWIFIList()
    }

    override fun onDestroy() {
        unregisterWIFIBroadcast()
        super.onDestroy()
    }

    private fun initRecyclerView () {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener { loadWIFIList(true) }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = WIFIListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mProgressView = findViewById(R.id.progress_view)
    }

    fun setActivityResult(wifiInfo: WIFIInfo) {
        val intent = intent.putExtra("wifiInfo", wifiInfo)
        setResult(RESULT_OK, intent)
        finish()
    }
//    fun connectToWIFI(wifiInfo: WIFIInfo, password: String?) {
//        wifiInfo.password = password
//        mProgressDialog.show()
//
//
//        switchToWIFI(wifiInfo)
//    }

//    private fun switchToWIFI(wifiInfo: WIFIInfo) {
//        val intent = Intent(this, SettingsActivity::class.java)
//        intent.putExtra("wifi", wifiInfo)
////        intent.putExtra("old_ip", mDeviceManager!!.tcpClient.ip)
//        startActivityForResult(intent, REQUEST_CODE_SWITCH_WIFI)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_CODE_SWITCH_WIFI) {
//            mProgressDialog.dismiss()
//            if (resultCode == RESULT_OK) {
//                //startActivity(Intent(this, MainActivity::class.java))
//                Log.d("WIFIListActivity", "[onActivityResult] RESULT_OK")
//                finish()
//            } else if (resultCode != Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "Error: failed to switch to specified Wi-Fi", Toast.LENGTH_LONG).show()
//                Log.d("WIFIListActivity", "[onActivityResult] Error: failed to switch to specified Wi-Fi")
//            } else {
//                Toast.makeText(this, "Error: failed to set specified Wi-Fi", Toast.LENGTH_LONG).show()
//                Log.d("WIFIListActivity", "[onActivityResult] Error: failed to set specified Wi-Fi")
//            }
//        } else if (requestCode == NetworkUtils.REQUEST_LOCATION_SETTINGS) {
//            if (resultCode == RESULT_OK) {
//                loadWIFIList()
//            } else {
//                finish()
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            swipeRefreshLayout.isRefreshing = false
            if (success) {
//                Log.d("WIFIListActivity", "[wifiScanReceiver.onReceive] Sucess! WifiManager.EXTRA_RESULTS_UPDATED")

                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val results = wifiManager.scanResults
                var wifiInfoList = ArrayList<WIFIInfo>()
                Log.d(TAG, "results.size: ${results.size}")
                for (result in results) {
                    if (result.BSSID == wifiManager.connectionInfo.bssid) {
                        val level = WifiManager.calculateSignalLevel(wifiManager.connectionInfo.rssi, result.level)
                        val difference = level * 100 / result.level
                        var signalStrangth = 0
                        if (difference >= 100) signalStrangth =
                            4 else if (difference >= 75) signalStrangth =
                            3 else if (difference >= 50) signalStrangth =
                            2 else if (difference >= 25) signalStrangth = 1

                    }
                    if(result.SSID.isNullOrEmpty()) continue
                    var wifiInfo = WIFIInfo()
                    wifiInfo.ssid = result.SSID
                    wifiInfo.bssid = result.BSSID

                    val level = WifiManager.calculateSignalLevel(result.level, 5)
                    when(level) {
                        0->wifiInfo.bars = "____"
                        1->wifiInfo.bars = "▂___"
                        2->wifiInfo.bars = "▂▄__"
                        3->wifiInfo.bars = "▂▄▆_"
                        4->wifiInfo.bars = "▂▄▆█"
                        else->wifiInfo.bars = "____"
                    }

                    wifiInfo.capabilities = result.capabilities
                    Log.d(TAG, String.format("got Wi-Fi, ssid=%s, bssid=%s, capabilities=%s ", result.SSID, result.BSSID, result.capabilities))
                    wifiInfoList.add(wifiInfo)
//                    Log.d("WIFIListActivity", String.format("got Wi-Fi, ssid=%s, bssid=%s, capabilities=%s security=%s passwordRequired=%s", result.SSID, result.BSSID, result.capabilities))
//                    " wifiInfo.security: "+ wifiInfo.security+ " wifiInfo.passwordRequired: "
                }
                (recyclerView.adapter as WIFIListAdapter).setList(wifiInfoList)
                Log.d(TAG, "results.size: ${results.size}")
                if (results.size == 0) {
                    Toast.makeText(this@WIFIListActivity, "There is no Wi-Fi scanned", Toast.LENGTH_SHORT).show()
                    Log.d("WIFIListActivity", "[wifiScanReceiver.onReceive] There is no Wi-Fi scanned")
//                    finish()
                }
            } else {
                (recyclerView.adapter as WIFIListAdapter).setList(ArrayList())
                Toast.makeText(this@WIFIListActivity, "Failed to scan Wi-Fi", Toast.LENGTH_LONG).show()
                Log.d("WIFIListActivity", "[wifiScanReceiver.onReceive] Failed to scan Wi-Fi")
            }

            mProgressView.visibility = View.INVISIBLE

        }
    }

    private fun registerWIFIBroadcast () {
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
    }

    private fun unregisterWIFIBroadcast () {
        unregisterReceiver(wifiScanReceiver)
    }

    private fun loadWIFIList(isSwipeRefresh: Boolean = false) {
        if (!isSwipeRefresh)
            mProgressView.visibility = View.VISIBLE

        NetworkUtils.checkLocationSetting(this)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val success = wifiManager.startScan()
        if (!success) {
            swipeRefreshLayout.isRefreshing = false
            mProgressView.visibility = View.INVISIBLE
            Toast.makeText(this, "Failed to scan Wi-Fi", Toast.LENGTH_LONG).show()
            Log.d("WIFIListActivity", "[loadWIFIList] Failed to scan Wi-Fi")
        }
    }

}