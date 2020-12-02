package com.ulsee.shiba.model

import android.content.SharedPreferences

class PermissionAskPreference (prefs: SharedPreferences) {

    private var sharedPreferences : SharedPreferences

    init {
        sharedPreferences = prefs
    }

    fun isPermissionEverAsked(permission: String): Boolean {
        return sharedPreferences.getBoolean(permission, false)
    }

    fun setPermissionAsked(permission: String) {
        sharedPreferences.edit().putBoolean(permission, true).apply()
    }

}