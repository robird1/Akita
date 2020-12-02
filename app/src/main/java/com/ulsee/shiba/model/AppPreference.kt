package com.ulsee.shiba.model

import android.content.SharedPreferences

private const val CONNECTED_ADDRESS = "connected_address"

private const val PERMISSION_ONCE_CREATE_FIRST_DEVICE = "isOnceCreateFirstDevice"
private const val IS_FEVER_NOTIFICATION_ENABLED = "fever_notification"

class AppPreference (private val prefs: SharedPreferences) {
//    fun setConnectedAddress(address: String) {
//        prefs.edit().putString(CONNECTED_ADDRESS, address).apply()
//    }
//
//    fun getConnectedAddress(): String {
//        return prefs.getString(CONNECTED_ADDRESS, "")!!
//    }

    fun isOnceCreateFirstDevice(): Boolean {
        return prefs.getBoolean(PERMISSION_ONCE_CREATE_FIRST_DEVICE, false)
    }

    fun setOnceCreateFirstDevice() {
        prefs.edit().putBoolean(PERMISSION_ONCE_CREATE_FIRST_DEVICE, true).apply()
    }

    fun isFeverNotificationEnabled(): Boolean {
        return prefs.getBoolean(IS_FEVER_NOTIFICATION_ENABLED, false)
    }

    fun setIsFeverNotificationEnabled(value: Boolean) {
        prefs.edit().putBoolean(IS_FEVER_NOTIFICATION_ENABLED, value).apply()
    }
}