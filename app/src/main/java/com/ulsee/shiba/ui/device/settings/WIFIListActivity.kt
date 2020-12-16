package com.ulsee.shiba.ui.device.settings

import android.content.*
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ulsee.shiba.R
import com.ulsee.shiba.model.WIFIInfo
import com.ulsee.shiba.utils.NetworkUtils


private val TAG = WIFIListActivity::class.java.simpleName

class WIFIListActivity : AppCompatActivity() {
    private lateinit var viewModel: WifiListViewModel
    lateinit var recyclerView : RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mProgressView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_wifilist)
        initViewModel()
        initRecyclerView()
        registerWIFIBroadcast()
        observeStartScan()
        observerOnReceive()
        loadWifiList()
    }

    override fun onDestroy() {
        unregisterWIFIBroadcast()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NetworkUtils.REQUEST_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                loadWifiList()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            viewModel.onReceive(intent)
        }
    }

    private fun observeStartScan() {
        viewModel.startScanResult.observe(this, { success ->
            if (!success) {
                disableProgressView()
                Toast.makeText(this, "Failed to scan Wi-Fi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observerOnReceive() {
        viewModel.onReceiveResult.observe(this, { success ->
            disableProgressView()
            if (success) {
                val pair = viewModel.getWifiInfo(this@WIFIListActivity)

                (recyclerView.adapter as WifiListAdapter).setList(pair.second)
                Log.d(TAG, "results.size: ${pair.first.size}")
                if (pair.first.size == 0) {
                    Toast.makeText(this@WIFIListActivity, "There is no Wi-Fi scanned", Toast.LENGTH_SHORT).show()
                }
            } else {
                (recyclerView.adapter as WifiListAdapter).setList(ArrayList())
                Toast.makeText(this@WIFIListActivity, "Failed to get Wi-Fi list", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun disableProgressView() {
        swipeRefreshLayout.isRefreshing = false
        mProgressView.visibility = View.INVISIBLE
    }

    private fun loadWifiList(isSwipeRefresh: Boolean = false) {
        if (!isSwipeRefresh)
            mProgressView.isVisible = true
        viewModel.loadWIFIList(this)
    }

    private fun registerWIFIBroadcast () {
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
    }

    private fun unregisterWIFIBroadcast () {
        unregisterReceiver(wifiScanReceiver)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(WifiListViewModel::class.java)
    }

    private fun initRecyclerView () {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener { loadWifiList(true) }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = WifiListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mProgressView = findViewById(R.id.progress_view)
    }

    fun setActivityResult(wifiInfo: WIFIInfo) {
        val intent = intent.putExtra("wifiInfo", wifiInfo)
        setResult(RESULT_OK, intent)
        finish()
    }

}