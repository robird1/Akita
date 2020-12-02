package com.ulsee.shiba.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.R
import com.ulsee.shiba.model.AppPreference
import com.ulsee.shiba.model.PermissionAskPreference
import com.ulsee.shiba.model.RealmDevice
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class LaunchActivity : AppCompatActivity() {

    val NEEDED_PERMISSIONS = arrayOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val REQUEST_CODE_PERMISSION = 11111;
    val REQUEST_CODE_GO_PERMISSION_SETTINGS = 11112;

    lateinit var permissionAskPreference : PermissionAskPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        permissionAskPreference = PermissionAskPreference(getSharedPreferences("permission", Context.MODE_PRIVATE))

        // permission
        Timer().schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread { validatePermissions() }
            }
        }, 200)
    }

    private fun validatePermissions() {
        if (isPermissionsAllGranted()) {
            goNextPage()
        } else if (isUserRequestNeverAskPermissionAgain()) {
            Toast.makeText(
                this,
                "Miss permission, but You require never ask permission again",
                Toast.LENGTH_SHORT
            ).show()
            goPermissionSettings()
        } else {
            requestPermission()
        }
    }

    // TODO
    private fun goNextPage() {
        val appPreference = AppPreference(getSharedPreferences("app", MODE_PRIVATE))
        val hasDevice = Realm.getDefaultInstance().where<RealmDevice>().findAll().size > 0
//        if (appPreference.isOnceCreateFirstDevice() || hasDevice ) {
//            startActivity(Intent(this, MainActivity::class.java))
//        } else {
//            startActivity(Intent(this, StarterActivity::class.java))
//        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(
            intent,
            REQUEST_CODE_GO_PERMISSION_SETTINGS
        )
    }

    private fun getNotAllowedPermissions(): List<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return ArrayList()
        }
        val notAllowedPermisions: MutableList<String> =
            ArrayList()
        for (permission in NEEDED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notAllowedPermisions.add(permission)
            }
        }
        return notAllowedPermisions
    }

    private fun isPermissionsAllGranted(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else getNotAllowedPermissions().size == 0
    }

    private fun isUserRequestNeverAskPermissionAgain(): Boolean {
        var isAnyonePermissionAskable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isAnyonePermissionAskable = false
            val notAllowedPermisions =
                getNotAllowedPermissions()
            for (permission in notAllowedPermisions) {
                val isPermissionEverAsked: Boolean =
                    permissionAskPreference.isPermissionEverAsked(permission)
                isAnyonePermissionAskable =
                    isAnyonePermissionAskable || !(shouldShowRequestPermissionRationale(permission!!) == false && isPermissionEverAsked)
                if (!isPermissionEverAsked) {
                    permissionAskPreference.setPermissionAsked(permission)
                }
            }
        }
        return !isAnyonePermissionAskable
    }

    private fun requestPermission(): Boolean {
        val notAllowedPermisions =
            getNotAllowedPermissions()
        ActivityCompat.requestPermissions(
            this,
            notAllowedPermisions.toTypedArray(),
            REQUEST_CODE_PERMISSION
        )
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                validatePermissions()
            }
        }
    }

}