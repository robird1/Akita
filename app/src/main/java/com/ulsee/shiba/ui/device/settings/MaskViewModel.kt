package com.ulsee.shiba.ui.device.settings

import androidx.lifecycle.*
import com.ulsee.shiba.data.response.CommonSettings
import com.ulsee.shiba.data.response.GetComSettings
import com.ulsee.shiba.data.response.SetComSettings
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = MaskViewModel::class.java.simpleName

class MaskViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _getResult = MutableLiveData<CommonSettings>()
    val getResult: LiveData<CommonSettings>
        get() = _getResult
    private var _setResult = MutableLiveData<Boolean>()
    val setResult: LiveData<Boolean>
        get() = _setResult


    init {
        getConfig()
    }

    private fun getConfig() {
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

    fun setConfig(isChecked: Boolean) {
        viewModelScope.launch {
            try {
                val response = repository.setComSettings(createJsonRequestBody(isChecked))
                _setResult.value = isSetComSuccess(response)
            } catch (e: Exception) {
                _setResult.value = false
            }
        }
    }

    private fun isGetComSuccess(response: GetComSettings) = response.status == 0
    private fun isSetComSuccess(response: SetComSettings) = response.status == 0

    private fun createJsonRequestBody(): RequestBody {
        return "{\r\n    \"CameraId\" : 0\r\n}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createJsonRequestBody(isChecked: Boolean): RequestBody {
        val tmp = "{\r\n    \"CameraId\" : 0,\r\n    \"commonSettings\" : {\r\n        \"recogRespirator\" : \"$isChecked\"\r\n    }\r\n}"
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }
}


class MaskFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}