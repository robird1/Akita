package com.ulsee.ulti_a100.model

import android.content.SharedPreferences

private const val CONNECTED_ADDRESS = "connected_address"

class AppPreference (private val prefs: SharedPreferences) {
    fun setConnectedAddress(address: String) {
        prefs.edit().putString(CONNECTED_ADDRESS, address).apply()
    }

    fun getConnectedAddress(): String {
        return prefs.getString(CONNECTED_ADDRESS, "")!!
    }

}