package com.ulsee.ulti_a100.ui.record

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.data.response.DeviceInfo
import com.ulsee.ulti_a100.model.Device
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceListViewModel::class.java.simpleName

class DeviceListViewModel(private val repository: DeviceInfoRepository) : ViewModel() {
    private var _deviceList = MutableLiveData<List<Device>>()
    val deviceList : LiveData<List<Device>>
        get() = _deviceList
    private val jobList = ArrayList<Job>()

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

    fun getConnectionStatus(url: String, position: Int, viewHolder: RecordViewHolder) {
        val job = viewModelScope.launch  {
            while(true) {
                var isConnected: Boolean
                try {
                    val deviceInfo = repository.requestDeviceInfo(url)
                    isConnected = isDeviceOnline(deviceInfo)

                } catch (e: Exception) {
                    isConnected = false
                }

                Log.d(TAG, "isConnected: $isConnected position: $position")

                viewHolder.displayConnectionStatus(isConnected)
                delay(5000)
            }
        }
        jobList.add(job)
    }

    private fun isDeviceOnline(deviceInfo: DeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

}

class RecordListFactory(private val repository: DeviceInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}