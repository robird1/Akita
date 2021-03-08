package com.ulsee.shiba.ui.record

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ulsee.shiba.data.response.AttendRecord
import com.ulsee.shiba.data.response.ClearAttendRecord
import com.ulsee.shiba.data.response.GetAttendRecordCount
import com.ulsee.shiba.data.response.getUIConfig
import com.ulsee.shiba.ui.device.settings.SettingRepository
import com.ulsee.shiba.ui.people.ERROR_CODE_API_NOT_SUCCESS
import com.ulsee.shiba.ui.people.ERROR_CODE_EXCEPTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = AttendRecordViewModel::class.java.simpleName

class AttendRecordViewModel(private val repository: AttendRecordRepository) : ViewModel() {
    private var _recordCountResult = MutableLiveData<Boolean>()
    val recordCountResult : LiveData<Boolean>
        get() = _recordCountResult
    private var _temperatureUnit = MutableLiveData<String>()
    val temperatureUnit: LiveData<String>
        get() = _temperatureUnit
    private var _clearResult = MutableLiveData<Boolean>()
    val clearResult: LiveData<Boolean>
        get() = _clearResult

    private var currentSearchResult: Flow<PagingData<AttendRecord>>? = null
    private var totalCount = 0
    private var startId = 0
    private var endId = 0
    private var _errorCode = -1
    val errorCode: Int
        get() = _errorCode


    init {
        loadRecordCount()
    }

    fun getRecords(): Flow<PagingData<AttendRecord>> {
//        Log.d(TAG, "[Enter] getRecords()")
//    val lastResult = currentSearchResult
//    if (lastResult != null) {
//        return lastResult
//    }
        val newResult: Flow<PagingData<AttendRecord>> = repository.getSearchResultStream(totalCount, startId, endId).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun invalidatePagingSource() {
        repository.invalidatePagingSource()
    }

    fun loadRecordCount() {
//        Log.d(TAG, "[Enter] loadRecordCount")
        viewModelScope.launch {
            try {
                val response = repository.requestAttendRecordCount()
                val isSuccess = isQuerySuccess(response)
                if (isSuccess) {
                    Log.d(TAG, "query success!! total count: ${response.totalCount}")
                    totalCount = response.totalCount
                    startId = response.startId
                    endId = response.endId
                    _recordCountResult.value = true
                } else {
                    Log.d(TAG, "query failed!!")
                    _errorCode = ERROR_CODE_API_NOT_SUCCESS
                    _recordCountResult.value = false
                }

            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
                _errorCode = ERROR_CODE_EXCEPTION
                _recordCountResult.value = false
            }
        }
    }

    fun getTemperatureUnit() {
        viewModelScope.launch {
            try {
                val response = SettingRepository(repository.url).getDeviceConfig()
                if (isQuerySuccess(response)) {
                    _temperatureUnit.value = response.data.FaceUIConfig.temperatureUnit
                } else {
                    _temperatureUnit.value = "C"
                }

//                Log.d(TAG, "[Enter] getTemperatureUnit() unit: ${response.data.FaceUIConfig.temperatureUnit}")

            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _temperatureUnit.value = "C"
            }
        }
    }

    fun clearAllRecords() {
        viewModelScope.launch {
            try {
                val response = repository.clearAttendRecord(createClearRequestBody())
                val isSuccess = isClearSuccess(response)
                if (isSuccess) {
                    _clearResult.value = true
                } else {
                    _errorCode = ERROR_CODE_API_NOT_SUCCESS
                    _clearResult.value = false
                }
            } catch (e: Exception) {
                _errorCode = ERROR_CODE_EXCEPTION
                _clearResult.value = false
            }
        }
    }

    fun getRecordCount(): Int = totalCount

    fun resetErrorCode() {
        _errorCode = -1
    }

    private fun isQuerySuccess(response: getUIConfig) = response.status == 0
    private fun isClearSuccess(response: ClearAttendRecord) = response.status == 0
    private fun isQuerySuccess(response: GetAttendRecordCount) = response.status == 0

    private fun createClearRequestBody(): RequestBody {
        val tmp = "{\r\n    \"clearBy\" : \"AllRecords\"\r\n}"
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

}


class AttendRecordFactory(private val repository: AttendRecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}