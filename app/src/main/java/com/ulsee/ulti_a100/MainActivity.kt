package com.ulsee.ulti_a100

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ulsee.ulti_a100.databinding.ActivityMainBinding

private val TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.navigation_device, R.id.navigation_people, R.id.navigation_notification, R.id.navigation_settings))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.collapseActionView()
            when (destination.id) {
                R.id.navigation_people -> setTitle("People")
                R.id.navigation_record -> setTitle("Record")
                R.id.attend_record -> hideBottomNav()
                R.id.device_settings -> hideBottomNav()
                R.id.language_config -> hideBottomNav()
                R.id.temperature_config -> hideBottomNav()
                R.id.panel_ui_config -> hideBottomNav()
                R.id.others_config -> hideBottomNav()
                R.id.light_mode_config -> hideBottomNav()
                R.id.volume_config -> hideBottomNav()
                else -> showBottomNav()
            }

        }
    }

    fun setTitle (title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.textView_toolbar_title).text = title
    }

    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

}