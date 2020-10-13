package com.ulsee.ulti_a100.ui.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.data.DeviceInfoRepository
import com.ulsee.ulti_a100.databinding.DialogAddDeviceBinding
import com.ulsee.ulti_a100.databinding.FragmentDeviceListBinding
import com.google.android.material.textfield.TextInputLayout

private val TAG = DeviceFragment::class.java.simpleName
private const val PORT_NUMBER = 8080

class DeviceFragment : Fragment() {
    private lateinit var binding: FragmentDeviceListBinding
    private lateinit var viewModel: DeviceListViewModel
    private var dialog: AlertDialog? = null
    private val deviceRemovedReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.loadDevices()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "[Enter] onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "[Enter] onCreate")
        super.onCreate(savedInstanceState)
        context?.registerReceiver(deviceRemovedReceiver, IntentFilter("Device removed"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "[Enter] onCreateView")

        binding = FragmentDeviceListBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, DeviceListFactory(DeviceInfoRepository()))
            .get(DeviceListViewModel::class.java)
        viewModel.deviceList.observe(viewLifecycleOwner, {
            Log.d(TAG, "[Enter] viewModel.deviceList.observe")
            Log.d(TAG, "device size: ${it.size}")

            (binding.recyclerView.adapter as DeviceListAdapter).setList(it)
        })
        viewModel.addDeviceResult.observe(viewLifecycleOwner, { isAddSuccess ->
            if (isAddSuccess) {
                dialog?.dismiss()
                viewModel.loadDevices()
            } else {
                dialog?.let {
                    updateDialog(it, false)
                    val textInputLayoutIP = it.findViewById<TextInputLayout>(R.id.text_input_ip_address)
                    textInputLayoutIP?.error = "connection failed"
                }
            }
        })

        binding.recyclerView.adapter = DeviceListAdapter(viewModel, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.fab.setOnClickListener {
            showAddDeviceDialog()
        }

        (activity as MainActivity).title = "Device"

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "[Enter] onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "[Enter] onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "[Enter] onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "[Enter] onDestroyView")
    }

    override fun onDestroy() {
        Log.d(TAG, "[Enter] onDestroy")

        context?.unregisterReceiver(deviceRemovedReceiver)
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "[Enter] onDetach")
    }

    fun showAddDeviceDialog(isEdit: Boolean = false, deviceID: String = "") {
        val binding = DialogAddDeviceBinding.inflate(LayoutInflater.from(requireContext()))
        if (isEdit) {
            binding.dialogTitle.text = "Edit Device"
            binding.buttonAdd.text = "Edit"
        }

        dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog!!.show()

        binding.buttonAdd.setOnClickListener {
            val isInputValid = checkInput(binding)
            if (isInputValid) {
                updateDialog(dialog!!, true)
                val deviceName = binding.textInputDeviceName.editText?.text.toString()
                val url = "http://"+binding.textInputIpAddress.editText?.text.toString()+ ":"+ PORT_NUMBER
                Log.d(TAG, "url: $url")

                viewModel.addDevice(isEdit, deviceID, deviceName, url)
            }
        }

        binding.buttonCancel.setOnClickListener {
            viewModel.cancelAddDeviceJob()
            dialog!!.dismiss()
        }

        addTextChangedListener(binding)
    }

    private fun addTextChangedListener(binding: DialogAddDeviceBinding) {
        binding.textInputIpAddress.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                binding.textInputIpAddress.error = ""
            }
        })
        binding.textInputDeviceName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                binding.textInputDeviceName.error = ""
            }
        })
    }

    private fun checkInput(binding: DialogAddDeviceBinding): Boolean {
        val inputIP = binding.textInputIpAddress.editText?.text.toString()
        val inputName = binding.textInputDeviceName.editText?.text.toString()

        return if (Patterns.IP_ADDRESS.matcher(inputIP).matches() && inputName.isNotEmpty()) {
            true
        } else {
            if (!Patterns.IP_ADDRESS.matcher(inputIP).matches()) {
                binding.textInputIpAddress.error = "Invalid IP address"
            }
            if (inputName.isEmpty()) {
                binding.textInputDeviceName.error = "Device name is necessary"
            }
            false
        }
    }

    private fun updateDialog(dialog: AlertDialog, isConnecting: Boolean) {
        val inputIP = dialog.findViewById<TextInputLayout>(R.id.text_input_ip_address)
        val inputDeviceName = dialog.findViewById<TextInputLayout>(R.id.text_input_device_name)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        val btnAdd = dialog.findViewById<Button>(R.id.button_add)
        inputIP?.isEnabled = !isConnecting
        inputDeviceName?.isEnabled = !isConnecting
        progressBar?.visibility = if (isConnecting) View.VISIBLE else View.INVISIBLE
        btnAdd?.isEnabled = !isConnecting
    }

}