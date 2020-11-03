package com.ulsee.ulti_a100.ui.record

import android.util.Log
import androidx.paging.PagingSource
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.QueryAttendRecord
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//private const val RECORD_START_PAGE_INDEX = 1
private const val RECORD_QUERY_COUNT = 10
private val TAG = AttendRecordPagingSource::class.java.simpleName

class AttendRecordPagingSource(private val repository: AttendRecordRepository, private val totalCount: Int) : PagingSource<Int, AttendRecord>() {
    private var lastStartId: Int = -1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendRecord> {
        val startId = params.key ?: getDefaultStartIndex()
//        Log.d(TAG, "[Enter] load() startId: $startId")

        return try {
            val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to getRequestCount(startId))
            val records = repository.requestAttendRecord(requestBody)
//            Log.d(TAG, "record size: " + records.recordCount)
            lastStartId = startId

            LoadResult.Page(data = getSortedList(records), prevKey = getPreviousKey(startId), nextKey = getNextKey(startId))

        } catch (exception: IOException) {
            Log.d(TAG, "IOException: "+ exception.message)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d(TAG, "HttpException: "+ exception.message)
            LoadResult.Error(exception)
        }
    }

    private fun getSortedList(records: QueryAttendRecord): List<AttendRecord> {
        return records.data.sortedByDescending {
            parseTime(it.timestamp)?.time
        }
    }

    private fun getRequestCount(startId: Int): Int {
        return if (startId != -1) {
            RECORD_QUERY_COUNT
        } else {
            lastStartId
        }
    }

    private fun getPreviousKey(startId: Int) =
        if (startId == getDefaultStartIndex()) null else startId + RECORD_QUERY_COUNT

    private fun getNextKey(startId: Int): Int? {
        if (startId == -1) return null
        return if (startId > RECORD_QUERY_COUNT) { startId - RECORD_QUERY_COUNT } else -1
    }

    private fun getDefaultStartIndex() = totalCount - RECORD_QUERY_COUNT

//    private fun createJsonRequestBody(vararg params: Pair<String, Int>) =
//        JSONObject(mapOf(*params)).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    private fun createJsonRequestBody(vararg params: Pair<String, Int>): RequestBody {
        val tmp = JSONObject(mapOf(*params, "needImg" to true)).toString()
        Log.d(TAG, "JSONObject toString: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun parseTime(time: String): Date? {
        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return try {
            simpleDateFormat.parse(time)
        } catch (e: Exception) {
            simpleDateFormat.parse("1970-01-01 00:00:00")
        }
    }
}
