package com.ulsee.shiba.ui.device

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.shiba.utils.Event
import com.ulsee.shiba.data.response.GetDeviceInfo
import com.ulsee.shiba.model.Device
import com.ulsee.shiba.ui.record.DeviceStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TAG = DeviceListViewModel::class.java.simpleName
const val ERROR_CODE_NAME_EXISTS = 1000
const val ERROR_CODE_DEVICE_PAIRED = 1001
const val ERROR_CODE_CONNECTION_FAILED = 1002
const val POLLING_INTERVAL = 3000L

class DeviceListViewModel(private val repository: DeviceInfoRepository) : ViewModel() {
    private var _deviceList = MutableLiveData<List<Device>>()
    val deviceList : LiveData<List<Device>>
        get() = _deviceList
    private val jobList = ArrayList<Job>()

    private var _addDeviceResult = MutableLiveData<Event<Boolean>>()
    val addDeviceResult : LiveData<Event<Boolean>>
        get() = _addDeviceResult
    private var addDeviceJob: Job? = null
    private var _addDeviceErrorCode = -1
    val addDeviceErrorCode: Int
        get() = _addDeviceErrorCode

    private var _deleteDeviceResult = MutableLiveData<Event<Boolean>>()
    val deleteDeviceResult : LiveData<Event<Boolean>>
        get() = _deleteDeviceResult

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

    fun loadDevices() {
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

    fun cancelAddDeviceJob() {
        addDeviceJob?.cancel()
    }

    fun resetAddDeviceErrorCode() {
        _addDeviceErrorCode = -1
    }

    fun getConnectionStatus(device: Device) {
        val job = viewModelScope.launch  {
            while(true) {
                try {
                    val deviceInfo = repository.requestDeviceInfo(device.getIP())
                    val isOnline = isDeviceOnline(deviceInfo)
                    if (isOnline) {
                        _deviceStatus.value = DeviceStatus(deviceInfo.data.mac, true)
                    } else {
                        _deviceStatus.value = DeviceStatus(device.getMAC(), false)
                    }

                } catch (e: Exception) {
                    _deviceStatus.value = DeviceStatus(device.getMAC(), false)
                }

                delay(POLLING_INTERVAL)
            }
        }
        jobList.add(job)
    }

    fun addDevice(isEdit: Boolean, deviceID: String, inputName: String, url: String) {
        addDeviceJob = viewModelScope.launch  {
            try {
                val deviceInfo = repository.requestDeviceInfo(url)
                if (isDeviceOnline(deviceInfo)) {

                    if (!isEdit) {
                        if (!isNameDuplicate(inputName) && !isDevicePaired(deviceInfo)) {
                            repository.addDevice(deviceInfo.data, inputName, url)
                            _addDeviceResult.value = Event(true)
                        }
                    } else {
                        if (isEditNameValid(inputName, deviceID) && isEditDeviceNotPaired(deviceID, deviceInfo)) {
                            repository.editDevice(deviceID, inputName, url)
                            _addDeviceResult.value = Event(true)
                        }
                    }

                } else {
                    _addDeviceErrorCode = ERROR_CODE_CONNECTION_FAILED
                    _addDeviceResult.value = Event(false)
                }

            } catch (e: Exception) {
                _addDeviceErrorCode = ERROR_CODE_CONNECTION_FAILED
                _addDeviceResult.value = Event(false)
            }
        }
    }

    private suspend fun isEditDeviceNotPaired(deviceID: String, deviceInfo: GetDeviceInfo) =
        repository.queryDevice(deviceID).getMAC() == deviceInfo.data.mac || !isDevicePaired(deviceInfo)

    private suspend fun isEditNameValid(inputName: String, deviceID: String) = inputName == deviceID || !isNameDuplicate(inputName)

    private suspend fun isDevicePaired(deviceInfo: GetDeviceInfo): Boolean {
        val isPaired = repository.isDevicePaired(deviceInfo.data.mac)
        if (isPaired) {
            _addDeviceErrorCode = ERROR_CODE_DEVICE_PAIRED
            _addDeviceResult.value = Event(false)
        }
        return isPaired
    }

    private suspend fun isNameDuplicate(inputName: String): Boolean {
        val isDuplicate = repository.isDeviceNameExisting(inputName)
        if (isDuplicate) {
            _addDeviceErrorCode = ERROR_CODE_NAME_EXISTS
            _addDeviceResult.value = Event(false)
        }
        return isDuplicate
    }

    fun deleteDevice(deviceID: String) {
        viewModelScope.launch {
            repository.deleteDevice(deviceID)
            _deleteDeviceResult.value = Event(true)
        }
    }

    private fun isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

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