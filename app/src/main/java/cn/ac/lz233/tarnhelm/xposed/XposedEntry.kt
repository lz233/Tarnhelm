package cn.ac.lz233.tarnhelm.xposed

import cn.ac.lz233.tarnhelm.xposed.app.Android
import cn.ac.lz233.tarnhelm.xposed.app.Self
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            Config.myPackageName -> Self.init(lpparam)
            "android" -> Android.init(lpparam)
        }
    }

}