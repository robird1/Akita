package com.ulsee.ulti_a100

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.ulsee.ulti_a100.data.response.AttendRecord
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.model.AppPreference
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import com.ulsee.ulti_a100.ui.device.settings.SettingRepository
import com.ulsee.ulti_a100.ui.record.AttendRecordRepository
import com.ulsee.ulti_a100.ui.record.RecordInfoActivity
import com.ulsee.ulti_a100.utils.ImageTemp
import com.ulsee.ulti_a100.utils.NotificationCenter
import kotlinx.coroutines.*
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
    var deferred : Deferred<Unit>? = null
    var temperatureUnit = "C"
}

fun App.listenNotification() {
    job = GlobalScope.launch {
        
        val notificationInfoMap = HashMap<String,DeviceNotificationInfo>()

        while(true) {
            // 1. get all devices
            val deviceInfoRepository = DeviceInfoRepository()
            val deviceList = deviceInfoRepository.loadDevices()

            for (device in deviceList) {
                val url = device.getIP()

                if (!notificationInfoMap.containsKey(url)) {
                    notificationInfoMap[url] = DeviceNotificationInfo()
                }

                if (notificationInfoMap[url]!!.deferred != null) {
                    Log.d(TAG, "listenNotification, $url still working async... skip")
                    continue // not end
                }
                val deferred = async {
                    listenDeviceNotification(device, notificationInfoMap)
                    return@async
                }
                notificationInfoMap[url]!!.deferred = deferred
            }
            delay(3000)
        }
    }
}

private suspend fun App.listenDeviceNotification(device: Device, notificationInfoMap: HashMap<String, DeviceNotificationInfo>) {
    Log.d(TAG, "listenNotification.listenDeviceNotification, ${device.getIP()}")

    val recordQueryCount = 10
    // 1. check  connected
    var isConnected: Boolean
    try {
        val url = device.getIP()
        val deviceSettingRepository = SettingRepository(url)
        val deviceConfig = deviceSettingRepository.getDeviceConfig()
        notificationInfoMap[url]!!.minTemp = deviceConfig.data.FaceUIConfig.minBodyTemperature
        notificationInfoMap[url]!!.maxTemp = deviceConfig.data.FaceUIConfig.maxBodyTemperature
        notificationInfoMap[url]!!.temperatureUnit = deviceConfig.data.FaceUIConfig.temperatureUnit
        isConnected = true

    } catch (e: Exception) {
        isConnected = false
    }
    if (!isConnected) {
        Log.d(TAG, "listenNotification ${device.getIP()} disconnected, skip..")
        notificationInfoMap[device.getIP()]?.deferred = null // finish
        return
    }

    // 2. query notification
    val attendRecordRepository = AttendRecordRepository(device.getIP())
    val key = attendRecordRepository.url
    val isFirstFetchNotifications = notificationInfoMap[key]!!.startId == null
    try {
        if (isFirstFetchNotifications) { // fetch total count first
            val response = attendRecordRepository.requestAttendRecordCount()
            notificationInfoMap[key]!!.startId = response.totalCount
            Log.d(TAG, "listenNotification $key got totalCount ${response.totalCount}")
            notificationInfoMap[key]?.deferred = null // finish
            return
        }

        val startId = notificationInfoMap[key]!!.startId!!
        val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to recordQueryCount)
        Log.d(TAG, "listenNotification $key request from ${startId}, reqCount=${recordQueryCount}")
        val records = attendRecordRepository.requestAttendRecord(requestBody)

        if (records.recordCount == 0) {
            Log.d(TAG, "listenNotification $key, recordCount= 0")
            notificationInfoMap[key]?.deferred = null // finish
            return
        }

        Log.d(TAG, "listenNotification $key, recordCount= ${records.recordCount}, size=${records.data?.size}")

        if (records.data != null && records.data.isNotEmpty()) {
            for (notification in records.data) {
                var temp = notification.bodyTemperature.toDouble()
                if (notificationInfoMap[key]!!.temperatureUnit == "F") temp = temp * 9 /5 + 32
//                val isTempTooLow = temp < notificationInfoMap[key]!!.minTemp
                val isTempTooHigh = temp > notificationInfoMap[key]!!.maxTemp
                Log.d(TAG, "listenNotification $key, is temp too high = $isTempTooHigh, temp = $temp, temperatureUnit = ${notificationInfoMap[key]!!.temperatureUnit}, max = ${notificationInfoMap[key]!!.maxTemp} {notifiaction.timestamp}")
                if (isTempTooHigh) {
                    doNotify(notification, notificationInfoMap[key]!!.temperatureUnit)
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
    notificationInfoMap[key]?.deferred = null // finish
}

private fun App.createJsonRequestBody(vararg params: Pair<String, Int>): RequestBody {
    val tmp = JSONObject(mapOf(*params, "needImg" to true)).toString()
    Log.d(TAG, "JSONObject toString: $tmp")
    return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}
private fun App.isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"

private fun App.doNotify(notification: AttendRecord, tempUnit: String) {

    val appPreference = AppPreference(PreferenceManager.getDefaultSharedPreferences(this))
    val isNotificationEnabled = appPreference.isFeverNotificationEnabled()
    Log.d(TAG, "[Enter] listenNotification.doNotify, isNotificationEnabled= $isNotificationEnabled")
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
            bundle.putString("temperature", getTransformedTemperature(it.bodyTemperature, tempUnit))
            intent.putExtra("bundle", bundle)
        }
        notification.notificationContentText = notification.timestamp + " Abnormal temperature detected. (" + notification.name + getTransformedTemperature(notification.bodyTemperature, tempUnit) + ")"
        NotificationCenter.shared.show(mContext, intent, mContext.getString(R.string.title_alert_notification), notification)
    }
}

private fun App.getTransformedTemperature(value: String, temperatureUnit: String): String {
    return if (value.isNotEmpty()) {
        if (temperatureUnit == "F") {
            String.format("%.1f", celciusToFahrenheit(value.toFloat())) + " °F"
        } else {
            String.format("%.1f", value.toFloat()) + " °C"
        }
    } else {
        ""
    }
}
private fun App.celciusToFahrenheit(celsius: Float): Float {
    return celsius * 9 / 5 + 32
}