package com.ulsee.ulti_a100.ui.record

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.GetAttendRecordCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val TAG = AttendRecordViewModel::class.java.simpleName

class AttendRecordViewModel(private val repository: AttendRecordRepository) : ViewModel() {
    private var _recordCountResult = MutableLiveData<Boolean>()
    val recordCountResult : LiveData<Boolean>
        get() = _recordCountResult
    private var currentSearchResult: Flow<PagingData<AttendRecord>>? = null
    private var startId = -1
    private var totalCount = 0


    init {
        loadRecordCount()
    }

    fun getRecords(): Flow<PagingData<AttendRecord>> {
        val lastResult = currentSearchResult
        if (lastResult != null) {
            return lastResult
        }
        val newResult: Flow<PagingData<AttendRecord>> = repository.getSearchResultStream(totalCount).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    private fun loadRecordCount() {
        viewModelScope.launch {
            try {
                val response = repository.requestAttendRecordCount()
                val isSuccess = isQuerySuccess(response)
                if (isSuccess) {
                    Log.d(TAG, "query success!!")
                    startId = response.startId
                    totalCount = response.totalCount
                    _recordCountResult.value = true
                } else {
                    Log.d(TAG, "query failed!!")
                    _recordCountResult.value = false
                }

            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
                _recordCountResult.value = false
            }
        }
    }

    private fun isQuerySuccess(response: GetAttendRecordCount) = response.status == 0 && response.detail == "success"

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