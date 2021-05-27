package com.ulsee.ulti_a100.ui.device.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.databinding.FragmentDeviceSettingBinding
import com.google.android.material.snackbar.Snackbar
import com.ulsee.ulti_a100.BuildConfig

private val TAG = SettingFragment::class.java.simpleName
const val REQUEST_PERMISSION_WIFI = 9876

class SettingFragment: Fragment() {
    private lateinit var binding: FragmentDeviceSettingBinding
    private lateinit var viewModel: SettingViewModel
    private val args: SettingFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDeviceSettingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, SettingFactory(SettingRepository(args.url)))
            .get(SettingViewModel::class.java)
//        Log.d(TAG, "args.url: ${args.url}")
        binding.recyclerView.adapter = SettingAdapter(viewModel, this, args.url)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeDeviceConfig()

        (activity as MainActivity).setTitle("Device Settings")

        return binding.root
    }

    private fun observeDeviceConfig() {
        viewModel.config.observe(viewLifecycleOwner, { data ->
            if (data != null) {
                (binding.recyclerView.adapter as SettingAdapter).setConfig(data)
            } else {
                Toast.makeText(requireContext(), "Load config error", Toast.LENGTH_LONG).show()
            }
            binding.progressView.visibility = View.INVISIBLE
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "[Enter] onRequestPermissionsResult")

        for (i in permissions) {
            Log.d(TAG, "permission: $i")
        }
        for (i in grantResults) {
            Log.d(TAG, "grantResults: $i")
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // now, you have permission go ahead
            Log.d(TAG, "[Enter] grantResults[0] == PackageManager.PERMISSION_GRANTED")
            val action = SettingFragmentDirections.actionToConnectMode(args.url)
            findNavController().navigate(action)

        } else {
            if (shouldShowRequestPermissionRationale(permissions[0]!!)) {
                Log.d(TAG, "[Enter] now, user has denied permission (but not permanently!)")
//                requestPermission(activity, permissions[0]!!)
                val permissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE)
                requestPermissions(permissions, REQUEST_PERMISSION_WIFI)

            } else {
                Log.d(TAG, "[Enter] now, user has denied permission permanently!")
                showPermissionIsNecessary()
            }
        }

    }

    private fun showPermissionIsNecessary() {
        val snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            """You have previously declined this permission. You must approve this permission in "Permissions" in the app settings on your device.""",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            startActivity(
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

}