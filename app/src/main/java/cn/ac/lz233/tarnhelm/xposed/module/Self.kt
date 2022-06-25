package cn.ac.lz233.tarnhelm.xposed.module

import cn.ac.lz233.tarnhelm.xposed.Config
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

object Self {

    fun init() {
        XposedHelpers.findAndHookMethod("cn.ac.lz233.tarnhelm.App", Config.classLoader, "isXposedActive", XC_MethodReplacement.returnConstant(true))
    }

}