package com.ulsee.ulti_a100.ui.record

import android.util.Log
import androidx.paging.PagingSource
import com.ulsee.ulti_a100.data.response.AttendRecord
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

private const val RECORD_START_PAGE_INDEX = 1        // TODO start index should be -1
private const val RECORD_QUERY_COUNT = 10
private val TAG = AttendRecordPagingSource::class.java.simpleName

class AttendRecordPagingSource(private val repository: AttendRecordRepository) : PagingSource<Int, AttendRecord>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendRecord> {
        val startId = params.key ?: RECORD_START_PAGE_INDEX
        return try {
            val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to RECORD_QUERY_COUNT)
            val records = repository.requestAttendRecord(requestBody)
            Log.d(TAG, "record size: " + records.recordCount)

            LoadResult.Page(
                data = records.data,
                prevKey = if (startId == RECORD_START_PAGE_INDEX) null else startId - RECORD_QUERY_COUNT,
                nextKey = if (records.recordCount < RECORD_QUERY_COUNT) null else { startId + RECORD_QUERY_COUNT
                }
            )
        } catch (exception: IOException) {
            Log.d(TAG, "IOException: "+ exception.message)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d(TAG, "HttpException: "+ exception.message)
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            Log.d(TAG, "Exception: "+ exception.message)
            LoadResult.Error(exception)
        }
    }

//    private fun createJsonRequestBody(vararg params: Pair<String, Int>) =
//        JSONObject(mapOf(*params)).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    private fun createJsonRequestBody(vararg params: Pair<String, Int>): RequestBody {
        val tmp = JSONObject(mapOf(*params, "needImg" to true)).toString()
        Log.d(TAG, "JSONObject toString: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }


}
