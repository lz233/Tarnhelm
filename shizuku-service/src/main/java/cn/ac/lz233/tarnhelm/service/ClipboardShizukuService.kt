package cn.ac.lz233.tarnhelm.service

import android.app.AppOpsManager
import android.app.AppOpsManagerHidden

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.Keep
import cn.ac.lz233.tarnhelm.shizuku_service.BuildConfig
import dev.rikka.tools.refine.Refine
import kotlin.system.exitProcess

@Keep
class ClipboardShizukuService(private val context: Context) : IClipboardShizukuService.Stub() {
    private lateinit var appOpsManager: AppOpsManager
    private lateinit var packageManager: PackageManager
    private lateinit var shizukuCallback: ShizukuCallback
    @Keep
    private lateinit var opNotedListener: AppOpsManagerHidden.OnOpNotedListener

    override fun exit() {
        destroy()
    }

    override fun destroy() {
        // LogUtil._d("ClipboardShizukuService destroy")
        Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager).stopWatchingNoted(opNotedListener)
        exitProcess(0)
    }

    override fun start() {
        // LogUtil._d("ClipboardShizukuService init")
        appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        packageManager = context.packageManager
        // DO NOT convert it to lambda due to R8 will break it down
        opNotedListener = object : AppOpsManagerHidden.OnOpNotedListener {
            override fun onOpNoted(op: String?, uid: Int, packageName: String?, attributionTag: String?, flags: Int, result: Int) {
                shizukuCallback.onOpNoted(op, uid, packageName, attributionTag, flags, result)
            }
        }
        // Allow self to draw floating window
        Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)
            .setMode(
                AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                packageManager.getPackageUid(BuildConfig.APPLICATION_ID, 0),
                BuildConfig.APPLICATION_ID,
                AppOpsManager.MODE_ALLOWED
            )
        // Register AppOps Note listener
        Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)
            .startWatchingNoted(intArrayOf(30), opNotedListener)
    }

    override fun addCallback(shizukuCallback: ShizukuCallback) {
        this.shizukuCallback = shizukuCallback
    }
}