package cn.ac.lz233.tarnhelm.xposed.app

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

object Self {

    fun init(loadPackageParam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod("cn.ac.lz233.tarnhelm.App", loadPackageParam.classLoader, "isXposedActive", XC_MethodReplacement.returnConstant(true))
    }

}