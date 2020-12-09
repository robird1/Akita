package com.ulsee.ulti_a100.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.utils.PermissionController
import java.util.*


private val TAG = LaunchActivity::class.java.simpleName

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        // permission
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    PermissionController().requestPermission(this@LaunchActivity, Manifest.permission.CAMERA)
                }
            }
        }, 200)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "[Enter] onRequestPermissionsResult")

//        when (requestCode) {
//            REQUEST_CODE_PERMISSION -> {
//                validatePermissions()
//            }
//        }
        PermissionController().onRequestPermissionsResult(requestCode, permissions, grantResults, this, MainActivity::class.java)
    }

}