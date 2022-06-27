package cn.ac.lz233.tarnhelm.ui.settings

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.TwoStatePreference
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.process.ProcessEditTextActivity
import com.google.android.material.snackbar.Snackbar

class SettingsFragment(val rootView: View) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val workOnEditTextMenu: TwoStatePreference = findPreference("workModeEditTextMenu")!!
        val workOnXposed: TwoStatePreference = findPreference("workModeXposed")!!

        workOnEditTextMenu.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessEditTextActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessEditTextActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        }
        workOnXposed.isChecked = App.isXposedActive()
        workOnXposed.setOnPreferenceChangeListener { preference, newValue ->
            Snackbar.make(rootView, R.string.settingsWorkModeOpenLSPosed, Toast.LENGTH_SHORT).show()
            false
        }
    }
}