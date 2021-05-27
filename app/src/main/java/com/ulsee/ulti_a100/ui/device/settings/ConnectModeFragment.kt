package com.ulsee.ulti_a100.ui.device.settings

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.ulti_a100.ui.device.settings.WIFIListActivity
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.data.response.WifiConfig
import com.ulsee.ulti_a100.databinding.FragmentConnectModeBinding
import com.ulsee.ulti_a100.model.WIFIInfo
import kotlinx.android.parcel.Parcelize


private val TAG = ConnectModeFragment::class.java.simpleName
const val REQUEST_CODE_WIFI_LIST = 7777

class ConnectModeFragment: Fragment() {
    private lateinit var binding: FragmentConnectModeBinding
    private lateinit var viewModel: ConnectModeViewModel
    private val args: TimeSyncFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConnectModeBinding.inflate(inflater, container, false)
        initViewModel()
        initModeChangeListener()
//        initSsidListener()
        initFooterBtnListener()
        observeGetWifiConfig()
        observeSetWifiConfig()
        (activity as MainActivity).setTitle("Network Mode")
        return binding.root
    }

    private fun initViewModel() {
        binding.progressView.isVisible = true
        viewModel = ViewModelProvider(this, ConnectModeFactory(SettingRepository(args.url)))
            .get(ConnectModeViewModel::class.java)
    }

    private fun initFooterBtnListener() {
        binding.confirmBtn.setOnClickListener {
            binding.progressView.isVisible = true
            val ssid = binding.textInputLayoutSsid.editText?.text.toString()
            val pwd = binding.textInputLayoutPassword.editText?.text.toString()
            val gateway = binding.textInputLayoutGateway.editText?.text.toString()
            val mode = if (binding.unitGroup.checkedRadioButtonId == R.id.radioBtn_AP) {
                "ap"
            } else {
                "sta"
            }
            val modeData = ModeData(mode, ssid, pwd, gateway)
            viewModel.setWifiConfig(modeData)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_WIFI_LIST) {
//            mProgressDialog.dismiss()
            if (resultCode == RESULT_OK) {
                val wifiInfo = data?.getSerializableExtra("wifiInfo") as WIFIInfo
                binding.textInputLayoutSsid.editText?.setText(wifiInfo!!.ssid)
                binding.textInputLayoutPassword.editText?.setText("")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initSsidListener() {
        binding.textInputLayoutSsid.editText?.setOnClickListener {
            val intent = Intent(requireActivity(), WIFIListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_WIFI_LIST)
        }
    }

    private fun initModeChangeListener() {
        binding.unitGroup.setOnCheckedChangeListener { _, checkedId ->
            hideSoftKeyboard(requireActivity())
            clearAllEditTextFocus()

            val config = viewModel.config.value
            if (config != null) {
                if (checkedId == R.id.radioBtn_AP) {
                    binding.textInputLayoutSsid.editText?.setOnClickListener(null)
                    binding.textInputLayoutSsid.editText?.isCursorVisible = true
                    binding.textInputLayoutSsid.editText?.isFocusableInTouchMode = true
                    updateInfoAP(config)
                } else {
                    binding.textInputLayoutSsid.editText?.isCursorVisible = false
                    binding.textInputLayoutSsid.editText?.isFocusableInTouchMode = false
                    if (!binding.textInputLayoutSsid.editText?.hasOnClickListeners()!!) {
                        initSsidListener()
                    }
                    updateInfoSTA(config)
                }
            }
        }
    }

    private fun clearAllEditTextFocus() {
        binding.constrainlayout.clearFocus()
    }

    private fun hideSoftKeyboard(activity: Activity) {
        activity.currentFocus?.let {
            val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun observeGetWifiConfig() {
        viewModel.config.observe(viewLifecycleOwner, { config ->
            binding.progressView.isVisible = false
            if (config != null) {
                when (config.mode) {
                    "sta" -> {
                        binding.unitGroup.check(R.id.radioBtn_STA)
                        updateInfoSTA(config)
//                        checkSTAConfig(config)
                    }
                    "ap" -> {
                        binding.unitGroup.check(R.id.radioBtn_AP)
                        updateInfoAP(config)
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Obtain Wi-Fi configuration failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun observeSetWifiConfig() {
        viewModel.result.observe(viewLifecycleOwner, { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "configure Wi-Fi successfully", Toast.LENGTH_SHORT).show()
                val action = ConnectModeFragmentDirections.actionToDeviceSettings()
                findNavController().navigate(action)
            } else {
                binding.progressView.isVisible = false
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateInfoSTA(config: WifiConfig) {
        binding.textInputLayoutSsid.editText?.setText(config.sta.ssid)
        binding.textInputLayoutPassword.editText?.setText(config.sta.password)
        binding.textInputLayoutIp.editText?.setText(config.sta.ipAddress)
        binding.textInputLayoutMask.editText?.setText(config.sta.subnetMask)
        binding.textInputLayoutGateway.editText?.setText(config.sta.DefaultGateway)
        binding.textInputLayoutMac.editText?.setText(config.sta.MACAddress)
    }

    private fun updateInfoAP(config: WifiConfig) {
        binding.textInputLayoutSsid.editText?.setText(config.ap.ssid)
        binding.textInputLayoutPassword.editText?.setText(config.ap.password)
        binding.textInputLayoutIp.editText?.setText(config.ap.ipAddress)
        binding.textInputLayoutMask.editText?.setText(config.ap.subnetMask)
        binding.textInputLayoutGateway.editText?.setText(config.ap.DefaultGateway)
        binding.textInputLayoutMac.editText?.setText(config.ap.MACAddress)
    }


    @Parcelize
    data class ModeData(
        var mode: String,
        var ssid: String,
        var password: String,
        var defaultGateway: String
    ): Parcelable

}
