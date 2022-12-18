package cn.ac.lz233.tarnhelm.xposed.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge
import cn.ac.lz233.tarnhelm.xposed.module.Android

@SuppressLint("StaticFieldLeak")
object ModuleBridgeHelper {

    private var bridge: ModuleDataBridge? = null
    var isBridgeAvailable = false
    var mContext: Context? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            bridge = ModuleDataBridge.Stub.asInterface(binder)
            isBridgeAvailable = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bridge = null
            isBridgeAvailable = false
            unbindBridgeService(mContext)
            Android.startModuleAppProcess()
            bindBridgeService()
        }
    }

    fun isBridgeActive(): Boolean {
        try {
            if (bridge == null) { return false }
            bridge!!.ping()
            return true
        } catch (thr: Throwable) {
            thr.printStackTrace()
            return false
        }
    }

    @SuppressLint("MissingPermission")
    fun bindBridgeService(context: Context? = mContext) {
        LogUtil.xp("bind bridge service")
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context?.bindServiceAsUser(
                    Intent().apply {
                        `package` = Config.packageName
                        action = Config.bridgeAction
                    },
                    serviceConnection,
                    Context.BIND_AUTO_CREATE,
                    android.os.Process.myUserHandle()
                )
            } else {
                context?.bindService(
                    Intent().apply {
                        `package` = Config.packageName
                        action = Config.bridgeAction
                    },
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }.onFailure { LogUtil.xpe(it) }
    }

    fun unbindBridgeService(context: Context? = mContext) {
        LogUtil.xp("unbind bridge service")
        runCatching {
            context?.unbindService(serviceConnection)
        }
    }

    fun doTarnhelms(string: String): String {
        if (!(isBridgeAvailable && isBridgeActive())) {
            Android.startModuleAppProcess()
            bindBridgeService()
        }
        bridge?.let {
            return it.doTarnhelms(string)
        }
        throw Exception("Bridge is not available")
    }

}