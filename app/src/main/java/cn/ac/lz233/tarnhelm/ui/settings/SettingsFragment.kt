package cn.ac.lz233.tarnhelm.ui.settings

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.TwoStatePreference
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.service.ClipboardService
import cn.ac.lz233.tarnhelm.ui.process.ProcessCopyActivity
import cn.ac.lz233.tarnhelm.ui.process.ProcessEditTextActivity
import cn.ac.lz233.tarnhelm.ui.process.ProcessShareActivity
import cn.ac.lz233.tarnhelm.ui.settings.backup.BackupBottomSheetFragment
import cn.ac.lz233.tarnhelm.util.ktx.openUrl
import com.google.android.material.snackbar.Snackbar
import rikka.shizuku.Shizuku

class SettingsFragment() : PreferenceFragmentCompat() {
    private lateinit var rootView: View

    private val shizukuPermissionCallback: (Int, Int) -> Unit = { requestCode, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            val workModeBackgroundMonitoring: TwoStatePreference = findPreference("workModeBackgroundMonitoring")!!
            workModeBackgroundMonitoring.isChecked = true
            activateClipboardService()
        } else {
            Snackbar.make(rootView, R.string.settingsWorkModeBackgroundMonitoringToast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val workModeEditTextMenu: TwoStatePreference = findPreference("workModeEditTextMenu")!!
        val workModeCopyMenu: TwoStatePreference = findPreference("workModeCopyMenu")!!
        val workModeShare: TwoStatePreference = findPreference("workModeShare")!!
        val workModeBackgroundMonitoring: TwoStatePreference = findPreference("workModeBackgroundMonitoring")!!
        val workModeXposed: TwoStatePreference = findPreference("workModeXposed")!!
        val xposed: PreferenceCategory = findPreference("xposed")!!
        val rewriteClipboard: TwoStatePreference = findPreference("rewriteClipboard")!!
        val overrideClipboardOverlay: TwoStatePreference = findPreference("overrideClipboardOverlay")!!
        val alwaysSendProcessingNotification: TwoStatePreference = findPreference("alwaysSendProcessingNotification")!!
        val systemNotificationSettings: Preference = findPreference("systemNotificationSettings")!!
        val exportRulesAsLink: TwoStatePreference = findPreference("exportRulesAsLink")!!
        val enableDefaultUA: TwoStatePreference = findPreference("enableDefaultUA")!!
        val backupAndRestore: Preference = findPreference("backupAndRestore")!!
        val website: Preference = findPreference("website")!!
        val telegramChannel: Preference = findPreference("telegramChannel")!!

        workModeEditTextMenu.setOnPreferenceChangeListener { preference, newValue ->
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

        workModeCopyMenu.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessCopyActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessCopyActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        }

        workModeShare.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessShareActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                App.context.packageManager.setComponentEnabledSetting(
                    ComponentName(App.context, ProcessShareActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        }

        if (!checkClipboardPrivilege() && workModeBackgroundMonitoring.isChecked) {
            Snackbar.make(rootView, R.string.settingsWorkModeBackgroundMonitoringToast, Toast.LENGTH_SHORT).show()
        }
        workModeBackgroundMonitoring.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue as Boolean) {
                if (checkClipboardPrivilege(true)) {
                    activateClipboardService()
                    true
                } else {
                    false
                }
            } else {
                deactivateClipboardService()
                true
            }
        }

        workModeXposed.isChecked = App.isXposedActive()
        workModeXposed.setOnPreferenceChangeListener { preference, newValue ->
            if (!workModeXposed.isChecked) Snackbar.make(rootView, R.string.settingsWorkModeOpenLSPosedToast, Toast.LENGTH_SHORT).show()
            false
        }

        xposed.isVisible = workModeXposed.isChecked

        rewriteClipboard.setOnPreferenceChangeListener { preference, newValue ->
            App.editorXposed?.putBoolean("rewriteClipboard", newValue as Boolean)?.apply()
            true
        }

        // Clipboard overlay is a feature introduced in Android 13
        overrideClipboardOverlay.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        overrideClipboardOverlay.setOnPreferenceChangeListener { preference, newValue ->
            App.editorXposed?.putBoolean("overrideClipboardOverlay", newValue as Boolean)?.apply()
            true
        }

        systemNotificationSettings.setOnPreferenceClickListener {
            startActivity(
                Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                }
            )
            true
        }

        backupAndRestore.setOnPreferenceClickListener {
            BackupBottomSheetFragment().show(childFragmentManager, "BackupBottomSheetFragment")
            true
        }

        website.setOnPreferenceClickListener {
            "https://tarnhelm.project.ac.cn".openUrl()
            true
        }

        telegramChannel.setOnPreferenceClickListener {
            "https://t.me/tarnhelm_app".openUrl()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get the root of this fragment and update its padding for more AOSP like appearance
        (((this.requireView() as LinearLayout).getChildAt(0) as FrameLayout).getChildAt(0) as RecyclerView).apply {
            updatePadding(top = resources.getDimensionPixelSize(R.dimen.collapsingToolbarLayoutContentPaddingTop))
            clipToPadding = false
        }
        Shizuku.addRequestPermissionResultListener(shizukuPermissionCallback);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionCallback);
    }

    private fun activateClipboardService() {
        App.context.packageManager.setComponentEnabledSetting(
            ComponentName(App.context, ClipboardService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        App.context.startForegroundService(Intent(App.context, ClipboardService::class.java))
        val workModeXposed: TwoStatePreference = findPreference("workModeXposed")!!
        if (workModeXposed.isChecked)
            Snackbar.make(rootView, R.string.settingsWorkModeRecommendationToast, Toast.LENGTH_SHORT).show()
    }

    private fun deactivateClipboardService() {
        App.context.packageManager.setComponentEnabledSetting(
            ComponentName(App.context, ClipboardService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        App.context.stopService(Intent(App.context, ClipboardService::class.java))
    }

    private fun checkClipboardPrivilege(request: Boolean = false): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        checkShizukuPermission(request)
    } else {
        true
    }

    private fun checkShizukuPermission(request: Boolean = false): Boolean = if (!Shizuku.pingBinder()) {
        false
    } else if (Shizuku.isPreV11()) {
        false
    } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        true
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
        Snackbar.make(rootView, R.string.settingsWorkModeBackgroundMonitoringToast, Toast.LENGTH_SHORT).show()
        false
    } else if (request) {
        Shizuku.requestPermission(233)
        false
    } else {
        false
    }

    constructor(rootView: View) : this() {
        this.rootView = rootView
    }
}