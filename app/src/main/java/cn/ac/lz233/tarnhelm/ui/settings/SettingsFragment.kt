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
import cn.ac.lz233.tarnhelm.util.AppCenterUtil
import cn.ac.lz233.tarnhelm.util.ktx.openUrl
import com.google.android.material.snackbar.Snackbar

class SettingsFragment() : PreferenceFragmentCompat() {
    private lateinit var rootView: View
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
        val useForegroundServiceOnBackgroundMonitoring: TwoStatePreference = findPreference("useForegroundServiceOnBackgroundMonitoring")!!
        val alwaysSendProcessingNotification: TwoStatePreference = findPreference("alwaysSendProcessingNotification")!!
        val systemNotificationSettings: Preference = findPreference("systemNotificationSettings")!!
        val exportRulesAsLink: TwoStatePreference = findPreference("exportRulesAsLink")!!
        val analytics: TwoStatePreference = findPreference("analytics")!!
        val crashes: TwoStatePreference = findPreference("crashes")!!
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

        workModeBackgroundMonitoring.setOnPreferenceChangeListener { preference, newValue ->
            if (App.checkClipboardPermission()) {
                if (newValue as Boolean) {
                    App.context.packageManager.setComponentEnabledSetting(
                        ComponentName(App.context, ClipboardService::class.java),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    App.context.startService(Intent(App.context, ClipboardService::class.java))
                    if (workModeXposed.isChecked)
                        Snackbar.make(rootView, R.string.settingsWorkModeRecommendationToast, Toast.LENGTH_SHORT).show()
                } else {
                    App.context.packageManager.setComponentEnabledSetting(
                        ComponentName(App.context, ClipboardService::class.java),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    App.context.stopService(Intent(App.context, ClipboardService::class.java))
                }
                true
            } else {
                Snackbar.make(rootView, R.string.settingsWorkModeBackgroundMonitoringToast, Toast.LENGTH_SHORT)
                    .setAction(R.string.settingsWorkModeBackgroundMonitoringToastAction) {
                        "https://tarnhelm.project.ac.cn/introduction.html#%E5%90%8E%E5%8F%B0%E7%9B%91%E5%90%AC%E6%A8%A1%E5%BC%8F".openUrl()
                    }.show()
                false
            }
        }

        workModeXposed.isChecked = App.isXposedActive()
        workModeXposed.setOnPreferenceChangeListener { preference, newValue ->
            if (!workModeXposed.isChecked)
                Snackbar.make(rootView, R.string.settingsWorkModeOpenLSPosedToast, Toast.LENGTH_SHORT).show()
            false
        }

        xposed.isVisible = workModeXposed.isChecked

        rewriteClipboard.setOnPreferenceChangeListener { preference, newValue ->
            App.editorXposed?.putBoolean("rewriteClipboard", newValue as Boolean)?.apply()
            true
        }

        overrideClipboardOverlay.isVisible = Build.VERSION.SDK_INT >= 33
        overrideClipboardOverlay.setOnPreferenceChangeListener { preference, newValue ->
            App.editorXposed?.putBoolean("overrideClipboardOverlay", newValue as Boolean)?.apply()
            true
        }

        useForegroundServiceOnBackgroundMonitoring.setOnPreferenceChangeListener { preference, newValue ->
            if (workModeBackgroundMonitoring.isChecked) {
                if (newValue as Boolean) {
                    App.context.stopService(Intent(App.context, ClipboardService::class.java))
                    App.context.startForegroundService(Intent(App.context, ClipboardService::class.java))
                } else {
                    App.context.stopService(Intent(App.context, ClipboardService::class.java))
                    App.context.startService(Intent(App.context, ClipboardService::class.java))
                }
            }
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

        analytics.isVisible = BuildConfig.FLAVOR != "fdroid"
        analytics.setOnPreferenceChangeListener { preference, newValue ->
            AppCenterUtil.setAnalyticsEnabled(newValue as Boolean)
            true
        }

        crashes.isVisible = BuildConfig.FLAVOR != "fdroid"
        crashes.setOnPreferenceChangeListener { preference, newValue ->
            AppCenterUtil.setCrashesEnabled(newValue as Boolean)
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
        // get the root view of this fragment
        (((this.requireView() as LinearLayout).getChildAt(0) as FrameLayout).getChildAt(0) as RecyclerView).apply {
            updatePadding(top = resources.getDimensionPixelSize(R.dimen.collapsingToolbarLayoutContentPaddingTop))
            clipToPadding = false
        }
    }

    constructor(rootView: View) : this() {
        this.rootView = rootView
    }
}