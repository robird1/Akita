package com.ulsee.ulti_a100.ui.device

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.DeviceInfoRepository
import com.ulsee.ulti_a100.data.response.DeviceInfo
import com.ulsee.ulti_a100.data.response.Info
import com.ulsee.ulti_a100.model.Device
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceListViewModel::class.java.simpleName

class DeviceListViewModel(repository: DeviceInfoRepository) : DeviceInfoViewModel(repository) {
    private var _deviceList = MutableLiveData<List<Device>>()
    val deviceList : LiveData<List<Device>>
        get() = _deviceList
    private val jobList = ArrayList<Job>()

    private var _addDeviceResult = MutableLiveData<Boolean>()
    val addDeviceResult : LiveData<Boolean>
        get() = _addDeviceResult
    private var addDeviceJob: Job? = null

    init {
        Log.d(TAG, "[Enter] init()")
        loadDevices()
    }

    fun loadDevices() {
        Log.d(TAG, "[Enter] loadDevices()")
        cancelAllConnectionJobs()
        _deviceList.value = repository.loadDevices()
    }

    private fun cancelAllConnectionJobs() {
        for (j in jobList) {
            j.cancel()
        }
        jobList.clear()
    }

    fun cancelAddDeviceJob() {
        addDeviceJob?.cancel()
    }

    fun getConnectionStatus(url: String, position: Int, viewHolder: ViewHolder) {
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

    fun addDevice(isEdit: Boolean, deviceID: String, inputName: String, url: String) {
        addDeviceJob = viewModelScope.launch  {
            var isConnected: Boolean
            try {
                val deviceInfo = repository.requestDeviceInfo(url)
                isConnected = isDeviceOnline(deviceInfo)
                if (isConnected) {
                    if (!isEdit) {
                        saveDeviceInfo(deviceInfo.data, inputName, url, _addDeviceResult)
                    } else {
                        editDeviceInfo(deviceID, inputName, url, _addDeviceResult)
                    }
                } else {
                    _addDeviceResult.value = false
                }
            } catch (e: Exception) {
                isConnected = false
                _addDeviceResult.value = false
            }

            Log.d(TAG, "isConnected: $isConnected")
        }
    }

    private fun saveDeviceInfo(info: Info, deviceName: String, url: String, liveDataResult: MutableLiveData<Boolean>) {
        repository.addDevice(info, deviceName, url, liveDataResult)
    }

    private fun editDeviceInfo(deviceID: String, deviceName: String, url: String, liveDataResult: MutableLiveData<Boolean>) {
        repository.editDevice(deviceID, deviceName, url, liveDataResult)
    }

    private fun isDeviceOnline(deviceInfo: DeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

}


class DeviceListFactory(private val repository: DeviceInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}