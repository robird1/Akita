package com.ulsee.shiba.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ulsee.shiba.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preference, rootKey)
    }
}