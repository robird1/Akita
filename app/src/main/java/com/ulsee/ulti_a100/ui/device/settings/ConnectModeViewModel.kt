package com.ulsee.ulti_a100.ui.device.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.GetWifiConfig
import com.ulsee.ulti_a100.data.response.SetWifiConfig
import com.ulsee.ulti_a100.data.response.WifiConfig
import com.ulsee.ulti_a100.model.WIFIInfo
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = ConnectModeViewModel::class.java.simpleName

class ConnectModeViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _config = MutableLiveData<WifiConfig>()
    val config: LiveData<WifiConfig>
        get() = _config
    private var _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean>
        get() = _result

    init {
        getWifiConfig()
    }

    fun getWifiConfig() {
        Log.d(TAG, "[Enter] getWifiConfig")

        viewModelScope.launch {
            try {
                val response = repository.getWifiConfig()
                if (isQuerySuccess(response)) {
                    _config.value = response.data.wifiConfig
                } else {
                    _config.value = null
                }


            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _config.value = null
            }
        }
    }

//    fun connectWifi(context: Context, wifiInfo: WIFIInfo) {
//        NetworkController(context).requestWifi(wifiInfo)
//    }

    fun setWifiConfig(modeData: ConnectModeFragment.ModeData) {
        Log.d(TAG, "[Enter] setWificonfig")

        viewModelScope.launch {
            try {
                val response = repository.setWifiConfig(createJsonRequestBody(modeData))
                _result.value = isQuerySuccess(response)

            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _result.value = true
            }
        }
    }

    private fun isQuerySuccess(response: GetWifiConfig) = response.status == 0
    private fun isQuerySuccess(response: SetWifiConfig) = response.status == 0

    private fun createJsonRequestBody(mode: ConnectModeFragment.ModeData): RequestBody {
        var tmp = ""
        tmp = if (mode.mode == "ap") {
            "{ \r\n    \"wifiConfig\": { \r\n        \"mode\":\"${mode.mode}\", \r\n        \"ap\": { \r\n            \"ssid\": \"${mode.ssid}\", \r\n            \"password\": \"${mode.password}\",\r\n            \"DefaultGateway\": \"${mode.defaultGateway}\"\r\n        }\r\n    } \r\n}"

        } else {
            "{ \r\n    \"wifiConfig\": { \r\n        \"mode\":\"${mode.mode}\", \r\n        \"sta\": { \r\n            \"ssid\": \"${mode.ssid}\", \r\n            \"password\": \"${mode.password}\"\r\n        }\r\n    } \r\n}"

        }

        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class ConnectModeFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectModeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConnectModeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}