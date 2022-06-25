package cn.ac.lz233.tarnhelm

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.room.Room
import cn.ac.lz233.tarnhelm.logic.AppDatabase
import cn.ac.lz233.tarnhelm.logic.dao.RuleDao
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object {
        lateinit var context: Context
        lateinit var sp: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        lateinit var db: AppDatabase
        lateinit var ruleDao: RuleDao
        lateinit var clipboard: ClipboardManager
        const val TAG = "Tarnhelm"

        @JvmStatic
        fun isXposedActive(): Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil._d("App start")
        context = applicationContext
        sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        editor = sp.edit()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tarnhelm").allowMainThreadQueries().build()
        ruleDao = db.ruleDao()
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        DynamicColors.applyToActivitiesIfAvailable(this)
        applicationContext.startService(
            Intent().apply {
                `package` = Config.packageName
                action = "cn.ac.lz233.tarnhelm.bridge"
            }
        )
    }
}