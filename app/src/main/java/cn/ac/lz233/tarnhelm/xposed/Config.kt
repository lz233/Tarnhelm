package cn.ac.lz233.tarnhelm.xposed

import cn.ac.lz233.tarnhelm.BuildConfig
import de.robv.android.xposed.XSharedPreferences

object Config {
    const val packageName = BuildConfig.APPLICATION_ID
    const val bridgeAction = "${BuildConfig.APPLICATION_ID}.bridge"
    lateinit var classLoader: ClassLoader
    val sp: XSharedPreferences
        get() = XSharedPreferences(BuildConfig.APPLICATION_ID, "${BuildConfig.APPLICATION_ID}_xposed")
}