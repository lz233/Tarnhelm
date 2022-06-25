package cn.ac.lz233.tarnhelm.xposed.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import cn.ac.lz233.tarnhelm.xposed.Config

object AMSHelper {

    private fun getApplicationInfo(ams: Any): ApplicationInfo {
        val context = ams.getObjectFieldAs<Context>("mContext")
        return context.packageManager.getApplicationInfo(Config.packageName, PackageManager.GET_META_DATA)
    }

    private fun getHostingRecord(ams: Any): Any {
        val context = ams.getObjectFieldAs<Context>("mContext")
        return "com.android.server.am.HostingRecord"
            .findClass(context.classLoader)
            .getConstructor(String::class.java, ComponentName::class.java)
            .newInstance("service", ComponentName(Config.packageName, "cn.ac.lz233.tarnhelm.service.ModuleDataBridgeService"))
    }

    // 8.1
    private fun startProcessLockedO(ams: Any) {
        ams.callMethod(
            "startProcessLocked",
            Config.packageName,
            getApplicationInfo(ams),
            true,
            0,
            "service",
            ComponentName(Config.packageName, "cn.ac.lz233.tarnhelm.service.ModuleDataBridgeService"),
            false,
            false,
            false,
            "others"
        )
    }

    // 9
    private fun startProcessLockedP(ams: Any) {
        startProcessLockedO(ams)
    }

    // 10
    private fun startProcessLockedQ(ams: Any) {
        ams.callMethod(
            "startProcessLocked",
            Config.packageName,
            getApplicationInfo(ams),
            true,
            0,
            getHostingRecord(ams),
            false,
            false,
            false,
            "others"
        )
    }

    // 11
    private fun startProcessLockedR(ams: Any) {
        ams.callMethod(
            "startProcessLocked",
            Config.packageName,
            getApplicationInfo(ams),
            true,
            0,
            getHostingRecord(ams),
            1,
            false,
            false,
            false,
            "others"
        )
    }

    // 12
    private fun startProcessLockedS(ams: Any) {
        ams.callMethod(
            "startProcessLocked",
            Config.packageName,
            getApplicationInfo(ams),
            true,
            0,
            getHostingRecord(ams),
            1,
            false,
            false,
            "others"
        )
    }

    fun startModuleAppProcess(ams: Any): Boolean {
        val func = when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.O_MR1 -> ::startProcessLockedO
            Build.VERSION_CODES.P -> ::startProcessLockedP
            Build.VERSION_CODES.Q -> ::startProcessLockedQ
            Build.VERSION_CODES.R -> ::startProcessLockedR
            Build.VERSION_CODES.S -> ::startProcessLockedS
            else -> ::startProcessLockedS
        }
        return startModuleAppProcessInner(ams, func)
    }

    private fun startModuleAppProcessInner(ams: Any, block: (Any) -> Unit): Boolean {
        return try {
            block(ams)
            true
        } catch (thr: Throwable) {
            false
        }
    }

}