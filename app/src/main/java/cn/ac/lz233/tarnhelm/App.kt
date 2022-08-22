package cn.ac.lz233.tarnhelm

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceManager
import androidx.room.Room
import cn.ac.lz233.tarnhelm.logic.AppDatabase
import cn.ac.lz233.tarnhelm.logic.dao.ParameterRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.RegexRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object {
        lateinit var context: Context
        lateinit var sp: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        lateinit var spSettings: SharedPreferences
        lateinit var db: AppDatabase
        lateinit var parameterRuleDao: ParameterRuleDao
        lateinit var regexRuleDao: RegexRuleDao
        lateinit var clipboard: ClipboardManager
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
        editor = sp.edit()
        db = Room.databaseBuilder(context, AppDatabase::class.java, "tarnhelm").allowMainThreadQueries().build()
        parameterRuleDao = db.parameterRuleDao()
        regexRuleDao = db.regexRuleDao()
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (isXposedActive()) context.startService(
            Intent().apply {
                `package` = Config.packageName
                action = "cn.ac.lz233.tarnhelm.bridge"
            }
        )
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}