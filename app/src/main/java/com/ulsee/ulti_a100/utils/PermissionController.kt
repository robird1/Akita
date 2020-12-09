package com.ulsee.ulti_a100.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.ulsee.ulti_a100.BuildConfig
import com.ulsee.ulti_a100.ui.LaunchActivity

const val REQUEST_CODE_PERMISSION = 11111;
private val TAG = LaunchActivity::class.java.simpleName

class PermissionController {

    fun requestPermission(activity: Activity, permission: String): Boolean {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            REQUEST_CODE_PERMISSION
        )
        return false
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
        activity: Activity,
        nextPage: Class<out Activity>
    ) {
        Log.d(TAG, "[Enter] onRequestPermissionsResult")

//        when (requestCode) {
//            REQUEST_CODE_PERMISSION -> {
//                validatePermissions()
//            }
//        }
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // now, you have permission go ahead
            Log.d(TAG, "[Enter] grantResults[0] == PackageManager.PERMISSION_GRANTED")
            goToNextPage(activity, nextPage)

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]!!)) {
                Log.d(TAG, "[Enter] now, user has denied permission (but not permanently!)")
                requestPermission(activity, permissions[0]!!)

            } else {
                Log.d(TAG, "[Enter] now, user has denied permission permanently!")
                showPermissionIsNecessary(activity)
            }
        }
        return
    }

    private fun showPermissionIsNecessary(activity: Activity) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            """You have previously declined this permission. You must approve this permission in "Permissions" in the app settings on your device.""",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            activity.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
            )
        }
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
            5 //Or as much as you need
        snackbar.show()
    }

    private fun goToNextPage(activity: Activity, nextPage: Class<out Activity>) {
        activity.let {
            it.startActivity(Intent(it, nextPage))
            it.finish()
        }
    }


}