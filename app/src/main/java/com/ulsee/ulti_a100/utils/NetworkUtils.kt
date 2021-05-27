package com.ulsee.ulti_a100.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

private val TAG = "NetworkUtils"

class NetworkUtils {
    companion object {
        const val REQUEST_LOCATION_SETTINGS = 5678

        fun isLocationEnabled(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // This is new method provided in API 28
                val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
                lm.isLocationEnabled
            } else {
                // This is Deprecated in API 28
                val mode = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                (mode != Settings.Secure.LOCATION_MODE_OFF)
            }
        }

        fun checkLocationSetting(activity: Activity) {
            val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000)

            val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(
                mLocationRequest
            )
            settingsBuilder.setAlwaysShow(true)

            val result = LocationServices.getSettingsClient(activity).checkLocationSettings(
                settingsBuilder.build()
            )

            result.addOnCompleteListener {
                try {
                    val response = it.getResult(ApiException::class.java)
                    Log.d(TAG, "response: " + response.toString())

                } catch (ex: ApiException) {
                    when (ex.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.d(
                                TAG,
                                "Location settings are not satisfied. Show the user a dialog to upgrade location settings."
                            )
                            try {
                                val resolvableApiException = ex as ResolvableApiException
                                resolvableApiException.startResolutionForResult(
                                    activity,
                                    REQUEST_LOCATION_SETTINGS
                                )

                            } catch (e: IntentSender.SendIntentException) {
                                Log.d(TAG, "PendingIntent unable to execute request.")
//                                Toast.makeText(this, "Failed to switch Wi-Fi...", Toast.LENGTH_LONG).show()
//                                finish()
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            Log.d(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
//                            Toast.makeText(this, "Failed to switch Wi-Fi...", Toast.LENGTH_LONG).show()
//                            finish()
                        }
                    }
                }
            }

        }

    }

}