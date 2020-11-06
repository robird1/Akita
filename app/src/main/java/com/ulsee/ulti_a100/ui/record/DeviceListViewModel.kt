package com.ulsee.ulti_a100.ui.record

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.ui.device.POLLING_INTERVAL
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceListViewModel::class.java.simpleName

class DeviceListViewModel(private val repository: DeviceInfoRepository) : ViewModel() {
    private var _deviceList = MutableLiveData<List<Device>>()
    val deviceList : LiveData<List<Device>>
        get() = _deviceList
    private val jobList = ArrayList<Job>()
    private var _deviceStatus = MutableLiveData<DeviceStatus>()
    val deviceStatus: LiveData<DeviceStatus>
        get() = _deviceStatus


    init {
        Log.d(TAG, "[Enter] init()")
        loadDevices()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "[Enter] onCleared()")
    }

    private fun loadDevices() {
        Log.d(TAG, "[Enter] loadDevices()")
        cancelAllConnectionJobs()
        viewModelScope.launch {
            _deviceList.value = repository.loadDevices()
        }
    }

    fun cancelAllConnectionJobs() {
        for (j in jobList) {
            j.cancel()
        }
        jobList.clear()
    }

    fun getConnectionStatus(device: Device) {
        val job = viewModelScope.launch  {
            while(true) {
                try {
//                    Log.d(TAG, "[Enter] requestDeviceInfo() MAC: ${device.getMAC()}")
                    val deviceInfo = repository.requestDeviceInfo(device.getIP())
                    val isOnline = isDeviceOnline(deviceInfo)
                    if (isOnline) {
                        _deviceStatus.value = DeviceStatus(deviceInfo.data.mac, true)
//                        Log.d(TAG, "MAC: ${deviceInfo.data.mac} isConnected: true")
                    } else {
                        _deviceStatus.value = DeviceStatus(device.getMAC(), false)
//                        Log.d(TAG, "MAC: ${device.getMAC()} isConnected: false")
                    }

                } catch (e: Exception) {
                    _deviceStatus.value = DeviceStatus(device.getMAC(), false)
//                    Log.d(TAG, "[Enter] Exception ============  MAC: ${device.getMAC()} isConnected: false")
                }

                delay(POLLING_INTERVAL)
            }
        }
        jobList.add(job)
    }

    private fun isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

}

data class DeviceStatus(val MAC: String, val isConnected: Boolean)

class DeviceListFactory(private val repository: DeviceInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}