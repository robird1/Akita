package com.ulsee.shiba.ui.device.settings

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.databinding.FragmentOthersConfigBinding
import kotlinx.android.parcel.Parcelize

private val TAG = OthersConfigFragment::class.java.simpleName

class OthersConfigFragment: Fragment() {
    private lateinit var binding: FragmentOthersConfigBinding
    private lateinit var viewModel: OthersViewModel
    private val args: OthersConfigFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOthersConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, OthersFactory(SettingRepository(args.othersConfig.url)))
            .get(OthersViewModel::class.java)

        initValues()
        observeSetDeviceConfig()

        binding.applyBtn.setOnClickListener {
            binding.progressView.visibility = View.VISIBLE
            viewModel.setDeviceConfig(getInput())
        }

        (activity as MainActivity).setTitle("Others")

        return binding.root
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = OthersConfigFragmentDirections.actionToDeviceSettings(args.othersConfig.url)
            findNavController().navigate(action)
        })
    }

    private fun initValues() {
        binding.checkBoxSingleWarn.isChecked = args.othersConfig.enableSingleWarn
        binding.checkBoxLiveness.isChecked = args.othersConfig.enableLiveness
    }

    private fun getInput(): OthersConfigData {
        return OthersConfigData(binding.checkBoxSingleWarn.isChecked, binding.checkBoxLiveness.isChecked,
            args.othersConfig.url)
    }

}


@Parcelize
data class OthersConfigData(var enableSingleWarn: Boolean, var enableLiveness: Boolean, var url: String): Parcelable