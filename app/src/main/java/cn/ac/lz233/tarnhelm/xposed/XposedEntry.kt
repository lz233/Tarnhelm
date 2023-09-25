package cn.ac.lz233.tarnhelm.xposed

import cn.ac.lz233.tarnhelm.xposed.module.Android
import cn.ac.lz233.tarnhelm.xposed.module.Self
import cn.ac.lz233.tarnhelm.xposed.module.SystemUI
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Config.classLoader = lpparam.classLoader
        when (lpparam.packageName) {
            Config.packageName -> Self.init()
            "android" -> Android.init()
            "com.android.systemui" -> SystemUI.init()
        }
    }

}