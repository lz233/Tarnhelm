package cn.ac.lz233.tarnhelm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import cn.ac.lz233.tarnhelm.logic.AppDatabase
import cn.ac.lz233.tarnhelm.logic.dao.RuleDao
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object {
        lateinit var context: Context
        lateinit var sp: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        lateinit var db: AppDatabase
        lateinit var ruleDao: RuleDao
        const val TAG = "Tarnhelm"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        editor = sp.edit()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tarnhelm").allowMainThreadQueries().build()
        ruleDao = db.ruleDao()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}