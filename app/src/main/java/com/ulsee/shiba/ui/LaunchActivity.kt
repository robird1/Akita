package com.ulsee.shiba.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.R
import java.util.*


private val TAG = LaunchActivity::class.java.simpleName

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                    finish()
                }
            }
        }, 200)
    }

}