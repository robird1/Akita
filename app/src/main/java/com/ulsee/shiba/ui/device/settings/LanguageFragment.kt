package com.ulsee.shiba.ui.device.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ulsee.shiba.MainActivity
import com.ulsee.shiba.R
import com.ulsee.shiba.databinding.FragmentLanguageConfigBinding

private val TAG = LanguageFragment::class.java.simpleName

class LanguageFragment: Fragment() {
    private lateinit var binding: FragmentLanguageConfigBinding
    private lateinit var viewModel: LanguageViewModel
    private val args: LanguageFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLanguageConfigBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, LanguageFactory(SettingRepository(args.url)))
            .get(LanguageViewModel::class.java)

        binding.radioGroup.check(getSelectedItem(args.language))

        observeSetDeviceConfig()

        binding.button2.setOnClickListener {
            binding.progressView.visibility = View.VISIBLE
            val language = getSelectedItem(binding.radioGroup.checkedRadioButtonId)
            viewModel.setDeviceConfig(language)
        }

        (activity as MainActivity).setTitle("Language")

        return binding.root
    }

    private fun observeSetDeviceConfig() {
        viewModel.result.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
            val action = LanguageFragmentDirections.actionToDeviceSettings(args.url)
            findNavController().navigate(action)
        })
    }

    private fun getSelectedItem(language: String): Int {
        return when(language) {
            getString(R.string.language_en) -> R.id.radioButton13
            getString(R.string.language_sc) -> R.id.radioButton12
            getString(R.string.language_tc) -> R.id.radioButton11
            getString(R.string.language_jp) -> R.id.radioButton10
            getString(R.string.language_ko) -> R.id.radioButton9
            getString(R.string.language_ru) -> R.id.radioButton8
            getString(R.string.language_it) -> R.id.radioButton7
            getString(R.string.language_es) -> R.id.radioButton6
            getString(R.string.language_de) -> R.id.radioButton5
            getString(R.string.language_fr) -> R.id.radioButton4
            getString(R.string.language_pt) -> R.id.radioButton3
            getString(R.string.language_tu) -> R.id.radioButton15
            getString(R.string.language_pl) -> R.id.radioButton14
            else -> R.id.radioButton13
        }
    }

    private fun getSelectedItem(resourceId: Int): String {
        return when(resourceId) {
            R.id.radioButton13 -> getString(R.string.language_en)
            R.id.radioButton12 -> getString(R.string.language_sc)
            R.id.radioButton11 -> getString(R.string.language_tc)
            R.id.radioButton10 -> getString(R.string.language_jp)
            R.id.radioButton9 -> getString(R.string.language_ko)
            R.id.radioButton8 -> getString(R.string.language_ru)
            R.id.radioButton7 -> getString(R.string.language_it)
            R.id.radioButton6 -> getString(R.string.language_es)
            R.id.radioButton5 -> getString(R.string.language_de)
            R.id.radioButton4 -> getString(R.string.language_fr)
            R.id.radioButton3 -> getString(R.string.language_pt)
            R.id.radioButton15 -> getString(R.string.language_tu)
            R.id.radioButton14 -> getString(R.string.language_pl)
            else -> getString(R.string.language_en)
        }
    }

}