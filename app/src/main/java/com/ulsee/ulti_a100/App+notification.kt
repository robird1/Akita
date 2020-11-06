package com.ulsee.ulti_a100

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.ui.record.AttendRecordRepository
import com.ulsee.ulti_a100.ui.record.RecordInfoActivity
import com.ulsee.ulti_a100.utils.NotificationCenter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


fun App.listenNotification() {
    job = GlobalScope.launch {
        
        val notificationInfoMap = HashMap<String,Int>()
        val recordQueryCount = 10

        while(true) {
            // 1. get all devices
            val deviceInfoRepository = DeviceInfoRepository()
            val deviceList = deviceInfoRepository.loadDevices()

            // 2. get all repository
            val attendRecordRepositoryList = ArrayList<AttendRecordRepository>()
            for (device in deviceList) {

                var isConnected: Boolean
                try {
                    val deviceInfo = deviceInfoRepository.requestDeviceInfo(device.getIP())
                    isConnected = isDeviceOnline(deviceInfo)
                } catch (e: Exception) {
                    isConnected = false
                }
                if (!isConnected) {
                    Log.d(TAG, "listenNotification ${device.getIP()} disconnected, skip..")
                    continue
                }

                val repository = AttendRecordRepository(device.getIP())
                attendRecordRepositoryList.add(repository)
            }

            // 3. get notification
            for (attendRecordRepository in attendRecordRepositoryList) {
                val key = attendRecordRepository.url
                val isFirstFetchNotifications = !notificationInfoMap.contains(key)
                try {
                    if (isFirstFetchNotifications) { // fetch total count first
                        val response = attendRecordRepository.requestAttendRecordCount()
                        notificationInfoMap[key] = response.totalCount
                        Log.d(TAG, "listenNotification $key got totalCount ${response.totalCount}")
                        continue
                    }

                    val startId = notificationInfoMap[key]!!
                    val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to recordQueryCount)
                    Log.d(TAG, "listenNotification $key request from ${startId}, reqCount=${recordQueryCount}")
                    val records = attendRecordRepository.requestAttendRecord(requestBody)

                    if (records.recordCount == 0) {
                        Log.d(TAG, "listenNotification $key, recordCount= 0")
                        continue
                    }

                    Log.d(TAG, "listenNotification $key, recordCount= ${records.recordCount}, size=${records.data?.size}")

                    if (records.data != null && records.data!!.isNotEmpty()) {
                        for (notification in records.data) {
                            Log.d(TAG, "listenNotification $key, do notify {notifiaction.timestamp}")
                            doNotify(notification)
                        }
                        notificationInfoMap[key] = notificationInfoMap[key]!!.plus(records.data.size)
                    }
                } catch (exception: IOException) {
                    Log.d(TAG, "listenNotification, $key IOException: "+ exception.message)
                } catch (exception: HttpException) {
                    Log.d(TAG, "listenNotification, $key HttpException: "+ exception.message)
                } catch (exception: Exception) {
                    Log.d(TAG, "listenNotification, $key Exception: "+ exception.message)
                }
            }
            delay(3000)
        }
    }
}

private fun App.createJsonRequestBody(vararg params: Pair<String, Int>): RequestBody {
    val tmp = JSONObject(mapOf(*params, "needImg" to true)).toString()
    Log.d(TAG, "JSONObject toString: $tmp")
    return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}
private fun App.isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

private fun App.doNotify(notification: AttendRecord) {
    Log.d(TAG, "[Enter] doNotify")
    val isNotificationEnabled = true//AppPreference(mContext.getSharedPreferences("app", Context.MODE_PRIVATE)).isFeverNotificationEnabled()
    if (isNotificationEnabled) {
        val mContext = this
        val intent = Intent(this, RecordInfoActivity::class.java)

        notification.let {
            val bundle = Bundle()
            bundle.putString("img", it.img)
            bundle.putString("name", it.name)
            bundle.putInt("age", it.age)
            bundle.putString("gender", it.gender)
            bundle.putString("country", it.country)
            bundle.putString("date", it.timestamp)
            intent.putExtra("bundle", bundle)
        }
        NotificationCenter.shared.show(mContext, intent, mContext.getString(R.string.title_alert_notification), notification)
    }
}
