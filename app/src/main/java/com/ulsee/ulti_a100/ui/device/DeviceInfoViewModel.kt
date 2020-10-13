package com.ulsee.ulti_a100.ui.device

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.DeviceInfoRepository
import com.ulsee.ulti_a100.data.response.DeviceInfo
import kotlinx.coroutines.launch

private val TAG = DeviceInfoViewModel::class.java.simpleName

open class DeviceInfoViewModel(val repository: DeviceInfoRepository) : ViewModel() {
    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message
    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    fun getDeviceStatus(url: String): Boolean {
        Log.d(TAG, "url: "+ url)

//        repository.updateUrl(url)

        var status = false
        viewModelScope.launch {
            try {
                val deviceInfo = repository.requestDeviceInfo(url)
                _status.value = isDeviceOnline(deviceInfo)
                status = isDeviceOnline(deviceInfo)

                _url.value = url

            } catch (e: Exception) {
                _message.value = e.message
                _status.value = false
            }
//            _url.value = url

            Log.d(TAG, "_status.value : "+ _status.value+ " message: "+ _message.value)
        }
        return status
    }

    private fun isDeviceOnline(deviceInfo: DeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

    fun saveDeviceInfo(deviceName: String, url: String) {
        repository.saveDeviceInfo(deviceName, url)
    }

}

class DeviceInfoFactory(private val repository: DeviceInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}