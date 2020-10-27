package com.ulsee.ulti_a100.ui.record

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.GetAttendRecordCount
import com.ulsee.ulti_a100.data.response.QueryAttendRecord
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

class AttendRecordRepository(private val url: String) {
    suspend fun requestAttendRecordCount(): GetAttendRecordCount {
        return ApiService.create(url).requestAttendRecordCount()
    }

    suspend fun requestAttendRecord(requestBody: RequestBody): QueryAttendRecord {
        return ApiService.create(url).requestAttendRecord(requestBody)
    }

    fun getSearchResultStream(): Flow<PagingData<AttendRecord>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { AttendRecordPagingSource(this) }
        ).flow
    }

    companion object {
        // PAGE_SIZE = 1 => 1 page per request
        private const val PAGE_SIZE = 1
    }
}