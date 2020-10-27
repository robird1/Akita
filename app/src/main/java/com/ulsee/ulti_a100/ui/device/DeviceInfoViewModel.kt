package com.ulsee.ulti_a100.ui.device

import androidx.lifecycle.*
import com.ulsee.ulti_a100.model.Device
import io.realm.Realm
import kotlinx.coroutines.launch

class DeviceInfoViewModel(private val repository: DeviceInfoRepository, private val deviceID: String) : ViewModel() {
    private var _deviceInfo = MutableLiveData<Device>()
    val deviceInfo : LiveData<Device>
        get() = _deviceInfo

    init {
        getDeviceInfo()
    }

    private fun getDeviceInfo() {
        viewModelScope.launch {
            try {
                _deviceInfo.value = repository.queryDevice(deviceID)
            } catch (e: Exception) {

            }
        }
    }
}


class DeviceInfoFactory(private val repository: DeviceInfoRepository, private val deviceID: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceInfoViewModel(repository, deviceID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}