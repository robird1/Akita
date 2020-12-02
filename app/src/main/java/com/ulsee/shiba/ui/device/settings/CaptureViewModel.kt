package com.ulsee.shiba.ui.device.settings

import androidx.lifecycle.*
import com.ulsee.shiba.data.response.CommonSettings
import com.ulsee.shiba.data.response.GetComSettings
import com.ulsee.shiba.data.response.SetComSettings
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = CaptureViewModel::class.java.simpleName

class CaptureViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _getResult = MutableLiveData<CommonSettings>()
    val getResult: LiveData<CommonSettings>
        get() = _getResult
    private var _setResult = MutableLiveData<Boolean>()
    val setResult: LiveData<Boolean>
        get() = _setResult


    init {
        getCaptureConfigs()
    }

    private fun getCaptureConfigs() {
        viewModelScope.launch {
            try {
                val response = repository.getComSettings(createJsonRequestBody())
                if (isGetComSuccess(response))
                    _getResult.value = response.data.commonSettings
                else
                    _getResult.value = null
            } catch (e: Exception) {
                _getResult.value = null
            }
        }
    }

    fun setCaptureConfigs(data: SetConfig) {
        viewModelScope.launch {
            try {
                val response = repository.setComSettings(createJsonRequestBody(data))
                _setResult.value = isSetComSuccess(response)
            } catch (e: Exception) {
                _setResult.value = false
            }
        }
    }

    private fun isGetComSuccess(response: GetComSettings) = response.status == 0 && response.detail == "success"
    private fun isSetComSuccess(response: SetComSettings) = response.status == 0 && response.detail == "success"

    private fun createJsonRequestBody(): RequestBody {
        return "{\r\n    \"CameraId\" : 0\r\n}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createJsonRequestBody(data: SetConfig): RequestBody {
        val tmp = "{\r\n    \"CameraId\" : 0,\r\n    \"commonSettings\" : {\r\n        \"attributeRecog\" : \"${data.attributeEnable}\",\r\n" +
                "        \"attrInterval\": ${data.attributeInterval},\r\n        \"recogBodyTemperature\" : \"${data.temperatureEnable}\",\r\n " +
                "       \"recogRespirator\" : \"${data.maskEnable}\",\r\n        \"enableStoreAttendLog\": \"${data.logEnable}\",\r\n" +
                "        \"attendInterval\": ${data.logInterval},\r\n        \"enableStoreStrangerAttLog\": \"${data.strangerEnable}\"\r\n    }\r\n}"
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }
}


data class SetConfig(val attributeEnable: Boolean, val attributeInterval: Int,
                     val temperatureEnable: Boolean, val maskEnable: Boolean, val logEnable:Boolean,
                     val logInterval: Int, val strangerEnable: Boolean)


class CaptureFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaptureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaptureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}