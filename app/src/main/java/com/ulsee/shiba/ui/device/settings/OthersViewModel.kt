package com.ulsee.shiba.ui.device.settings

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.shiba.data.response.SetUIConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = OthersViewModel::class.java.simpleName

class OthersViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean>
        get() = _result

    fun setDeviceConfig(data: OthersConfigData) {
        viewModelScope.launch {
            try {
                val response = repository.setDeviceConfig(createJsonRequestBody(data))
                _result.value = isQuerySuccess(response)

            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _result.value = false
            }
        }
    }

    private fun isQuerySuccess(response: SetUIConfig) = response.status == 0 && response.detail == "success"

    private fun createJsonRequestBody(data: OthersConfigData): RequestBody {
        val tmp = "{\r\n\r\n    \"FaceUIConfig\" : {\r\n        \"enableSingleWarning\": ${data.enableSingleWarn},\r\n" +
                "        \"enableLiveness\":${data.enableLiveness}\r\n    }\r\n\r\n}\r\n"
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class OthersFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OthersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OthersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}