package com.ulsee.shiba.ui.device.settings

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.shiba.data.response.SetUIConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = TemperatureViewModel::class.java.simpleName

class TemperatureViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean>
        get() = _result

    fun setDeviceConfig(max: String, min: String, offset: String, unit: String) {
        viewModelScope.launch {
            try {
                val response = repository.setDeviceConfig(createJsonRequestBody(max, min, offset, unit))
                _result.value = isQuerySuccess(response)

            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _result.value = false
            }
        }
    }

    private fun isQuerySuccess(response: SetUIConfig) = response.status == 0

    private fun createJsonRequestBody(max: String, min: String, offset: String, unit: String): RequestBody {
        val tmp = "{\r\n\r\n    \"FaceUIConfig\" : {\r\n\r\n       \"temperatureUnit\": \"${unit}\",\r\n\r\n       " +
                "\"minBodyTemperature\": ${min},\r\n\r\n       \"maxBodyTemperature\": ${max},\r\n\r\n       " +
                "\"offsetBodyTemperature\": ${offset}\r\n\r\n    }\r\n\r\n}\r\n"
//        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class TemperatureFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemperatureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemperatureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}