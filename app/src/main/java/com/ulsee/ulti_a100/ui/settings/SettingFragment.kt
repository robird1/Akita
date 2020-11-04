package com.ulsee.ulti_a100.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ulsee.ulti_a100.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preference, rootKey)
    }
}