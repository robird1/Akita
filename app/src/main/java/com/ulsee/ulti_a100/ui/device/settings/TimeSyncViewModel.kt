package com.ulsee.ulti_a100.ui.device.settings

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.Data3
import com.ulsee.ulti_a100.data.response.GetTime
import com.ulsee.ulti_a100.data.response.SetTime
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

private val TAG = TimeSyncViewModel::class.java.simpleName

class TimeSyncViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _deviceTime = MutableLiveData<Data3>()
    val deviceTime: LiveData<Data3>
        get() = _deviceTime
    private var _syncResult = MutableLiveData<Boolean>()
    val syncResult: LiveData<Boolean>
        get() = _syncResult


    init {
        getTime()
    }

    private fun getTime() {
        viewModelScope.launch {
            try {
                val response = repository.getTime()
                if (isGetTimeSuccess(response))
                    _deviceTime.value = response.data
                else
                    _deviceTime.value = null
            } catch (e: Exception) {
                _deviceTime.value = null
            }
        }
    }

    fun syncTime() {
        viewModelScope.launch {
            try {
                val response = repository.setTime(createJsonRequestBody())
                _syncResult.value = isSetTimeSuccess(response)
            } catch (e: Exception) {
                _syncResult.value = false
            }
        }
    }

    private fun isGetTimeSuccess(response: GetTime) = response.status == 0 && response.detail == "ok"
    private fun isSetTimeSuccess(response: SetTime) = response.status == 0 && response.detail == "ok"

    private fun createJsonRequestBody(): RequestBody {
        val c = Calendar.getInstance()
        val tmp = "{\r\n    \"year\" : ${c.get(Calendar.YEAR)},\r\n    \"month\" : ${c.get(Calendar.MONTH)+1},\r\n    " +
                "\"day\" : ${c.get(Calendar.DAY_OF_MONTH)},\r\n    \"hour\" : ${c.get(Calendar.HOUR_OF_DAY)},\r\n    " +
                "\"min\" : ${c.get(Calendar.MINUTE)},\r\n    \"sec\" : ${c.get(Calendar.SECOND)}\r\n}"
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class TimeSyncFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeSyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeSyncViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}