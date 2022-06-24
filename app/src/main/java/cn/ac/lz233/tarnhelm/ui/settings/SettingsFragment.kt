package cn.ac.lz233.tarnhelm.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import cn.ac.lz233.tarnhelm.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}