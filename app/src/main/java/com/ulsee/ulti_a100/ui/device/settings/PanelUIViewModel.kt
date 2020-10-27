package com.ulsee.ulti_a100.ui.device.settings

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.SetUIConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = PanelUIViewModel::class.java.simpleName

class PanelUIViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean>
        get() = _result

    fun setDeviceConfig(data: PanelUIData) {
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

    private fun createJsonRequestBody(data: PanelUIData): RequestBody {
        val tmp = "{\r\n\r\n    \"FaceUIConfig\" : {\r\n        \"show_body_temperature\": ${data.showTemperature},\r\n" +
                "        \"show_frame\": ${data.showFrame},\r\n        \"show_ip\": ${data.showIP},\r\n" +
                "        \"show_mac_address\": ${data.showMAC},\r\n        \"show_people_count\": ${data.showPeopleCount},\r\n" +
                "        \"show_recognize_area\": ${data.showArea},\r\n        \"show_recognize_result\": ${data.showResult}\r\n    }\r\n\r\n}\r\n"
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class PanelUIFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PanelUIViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PanelUIViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}