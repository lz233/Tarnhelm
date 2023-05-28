package cn.ac.lz233.tarnhelm

import android.app.Application
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.window.embedding.ActivityFilter
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitPlaceholderRule
import androidx.window.embedding.SplitRule
import androidx.window.layout.WindowInfoTracker
import cn.ac.lz233.tarnhelm.logic.AppDatabase
import cn.ac.lz233.tarnhelm.logic.dao.ExtensionDao
import cn.ac.lz233.tarnhelm.logic.dao.ParameterRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.RegexRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.ui.extensions.ExtensionsActivity
import cn.ac.lz233.tarnhelm.ui.main.MainActivity
import cn.ac.lz233.tarnhelm.ui.main.PlaceHolderActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity
import cn.ac.lz233.tarnhelm.ui.settings.SettingsActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions

class App : Application() {
    companion object {
        lateinit var context: Context
        lateinit var sp: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        lateinit var spSettings: SharedPreferences
        var spXposed: SharedPreferences? = null
        var editorXposed: SharedPreferences.Editor? = null
        lateinit var db: AppDatabase
        lateinit var parameterRuleDao: ParameterRuleDao
        lateinit var regexRuleDao: RegexRuleDao
        lateinit var extensionDao: ExtensionDao
        lateinit var clipboardManager: ClipboardManager
        lateinit var notificationManager: NotificationManager
        const val TAG = "Tarnhelm"

        @JvmStatic
        fun isEditTextMenuActive() = SettingsDao.workModeEditTextMenu

        @JvmStatic
        fun isCopyMenuActive() = SettingsDao.workModeCopyMenu

        @JvmStatic
        fun isShareActive() = SettingsDao.workModeShare

        @JvmStatic
        fun isBackgroundMonitoringActive() = SettingsDao.workModeBackgroundMonitoring

        @JvmStatic
        fun isXposedActive(): Boolean = false

        fun checkClipboardPermission() =
            (Build.VERSION.SDK_INT < 29) or (Settings.canDrawOverlays(context) && context.checkSelfPermission("android.permission.READ_LOGS") == PackageManager.PERMISSION_GRANTED)
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil._d("App started")

        context = applicationContext
        sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        spSettings = PreferenceManager.getDefaultSharedPreferences(context)
        runCatching {
            spXposed = context.getSharedPreferences("${BuildConfig.APPLICATION_ID}_xposed", MODE_WORLD_READABLE)
            editorXposed = spXposed?.edit()
        }
        editor = sp.edit()
        db = Room.databaseBuilder(context, AppDatabase::class.java, "tarnhelm").allowMainThreadQueries().build()
        parameterRuleDao = db.parameterRuleDao()
        regexRuleDao = db.regexRuleDao()
        extensionDao = db.extensionDao()

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (isXposedActive()) context.startService(
            Intent().apply {
                `package` = Config.packageName
                action = "cn.ac.lz233.tarnhelm.bridge"
            }
        )

        createSplitConfig()

        DynamicColors.applyToActivitiesIfAvailable(
            this,
            DynamicColorsOptions.Builder()
                .setThemeOverlay(R.style.Theme_Tarnhelm_DynamicColors)
                .setPrecondition { activity, theme ->
                    !activity.localClassName.startsWith("ui.process")
                }
                .build()
        )
    }

    private fun createSplitConfig() {
        WindowInfoTracker.getOrCreate(this)
            .windowLayoutInfo(this)
        val filterSet = setOf(
            SplitPairFilter(
                ComponentName(this, MainActivity::class.java),
                ComponentName(this, RulesActivity::class.java),
                null
            ),
            SplitPairFilter(
                ComponentName(this, MainActivity::class.java),
                ComponentName(this, ExtensionsActivity::class.java),
                null
            ),
            SplitPairFilter(
                ComponentName(this, MainActivity::class.java),
                ComponentName(this, SettingsActivity::class.java),
                null
            )
        )
        val splitAttributes = SplitAttributes.Builder()
            .setSplitType(SplitAttributes.SplitType.ratio(0.364f))
            //.setSplitType(SplitAttributes.SplitType.SPLIT_TYPE_HINGE)
            .setLayoutDirection(SplitAttributes.LayoutDirection.LOCALE)
            .build()
        val splitPairRule = SplitPairRule.Builder(filterSet)
            .setDefaultSplitAttributes(splitAttributes)
            //.setMinWidthDp(84)
            //.setMinSmallestWidthDp(600)
            //.setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ALWAYS_ALLOW)
            .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.ADJACENT)
            .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
            .setClearTop(true)
            .build()

        val placeholderActivityFilterSet = setOf(
            ActivityFilter(
                ComponentName(this, MainActivity::class.java),
                null
            )
        )
        val splitPlaceholderRule = SplitPlaceholderRule.Builder(placeholderActivityFilterSet, Intent(context, PlaceHolderActivity::class.java))
            .setDefaultSplitAttributes(splitAttributes)
            .setFinishPrimaryWithPlaceholder(SplitRule.FinishBehavior.ALWAYS)
            .setSticky(false)
            .build()

        RuleController.getInstance(this).apply {
            addRule(splitPairRule)
            addRule(splitPlaceholderRule)
        }
    }
}