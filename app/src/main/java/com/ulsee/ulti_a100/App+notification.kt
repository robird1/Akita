package com.ulsee.ulti_a100

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.ui.device.settings.SettingRepository
import com.ulsee.ulti_a100.ui.record.AttendRecordRepository
import com.ulsee.ulti_a100.ui.record.RecordInfoActivity
import com.ulsee.ulti_a100.utils.ImageTemp
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

class DeviceNotificationInfo {
    var startId : Int? = null
    var minTemp = 0.0
    var maxTemp = 0.0
}

fun App.listenNotification() {
    job = GlobalScope.launch {
        
        val notificationInfoMap = HashMap<String,DeviceNotificationInfo>()
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
                    val url = device.getIP()
                    val deviceSettingRepository = SettingRepository(url)
                    val deviceConfig = deviceSettingRepository.getDeviceConfig()
                    if (!notificationInfoMap.containsKey(url)) {
                        notificationInfoMap[url] = DeviceNotificationInfo()
                    }
                    notificationInfoMap[url]!!.minTemp = deviceConfig.data.FaceUIConfig.minBodyTemperature
                    notificationInfoMap[url]!!.maxTemp = deviceConfig.data.FaceUIConfig.maxBodyTemperature
                    isConnected = true
//                    val deviceInfo = deviceInfoRepository.requestDeviceInfo(device.getIP())
//                    isConnected = isDeviceOnline(deviceInfo)
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
                val isFirstFetchNotifications = notificationInfoMap[key]!!.startId == null
                try {
                    if (isFirstFetchNotifications) { // fetch total count first
                        val response = attendRecordRepository.requestAttendRecordCount()
                        notificationInfoMap[key]!!.startId = response.totalCount
                        Log.d(TAG, "listenNotification $key got totalCount ${response.totalCount}")
                        continue
                    }

                    val startId = notificationInfoMap[key]!!.startId!!
                    val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to recordQueryCount)
                    Log.d(TAG, "listenNotification $key request from ${startId}, reqCount=${recordQueryCount}")
                    val records = attendRecordRepository.requestAttendRecord(requestBody)

                    if (records.recordCount == 0) {
                        Log.d(TAG, "listenNotification $key, recordCount= 0")
                        continue
                    }

                    Log.d(TAG, "listenNotification $key, recordCount= ${records.recordCount}, size=${records.data.size}")

                    if (records.data.isNotEmpty()) {
                        for (notification in records.data) {
                            val temp = notification.bodyTemperature.toDouble()
                            val shouldNotify = temp < notificationInfoMap[key]!!.minTemp || temp > notificationInfoMap[key]!!.maxTemp
                            Log.d(TAG, "listenNotification $key, shouldNotify = $shouldNotify {notifiaction.timestamp}")
                            if (shouldNotify) {
                                doNotify(notification)
                            }
                        }
                        notificationInfoMap[key]!!.startId = notificationInfoMap[key]!!.startId?.plus(records.data.size)
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
//            bundle.putString("img", it.img)
            ImageTemp.imgBase64 = it.img
            bundle.putString("name", it.name)
            bundle.putInt("age", it.age)
            bundle.putString("gender", it.gender)
            bundle.putString("country", it.country)
            bundle.putString("date", it.timestamp)
            bundle.putString("temperature", it.bodyTemperature)
            intent.putExtra("bundle", bundle)
        }
        NotificationCenter.shared.show(mContext, intent, mContext.getString(R.string.title_alert_notification), notification)
    }
}
