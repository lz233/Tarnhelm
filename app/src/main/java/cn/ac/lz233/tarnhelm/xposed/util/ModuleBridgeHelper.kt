package cn.ac.lz233.tarnhelm.xposed.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.UserHandle
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge
import cn.ac.lz233.tarnhelm.xposed.module.Android
import org.lsposed.hiddenapibypass.HiddenApiBypass

@SuppressLint("StaticFieldLeak")
object ModuleBridgeHelper {

    var bridge: ModuleDataBridge? = null
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
        LogUtil._d("bind bridge service")
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HiddenApiBypass.addHiddenApiExemptions("")
                val userHandle = UserHandle::class.java.getDeclaredField("CURRENT").get(null) as UserHandle
                context?.bindServiceAsUser(
                    Intent().apply {
                        `package` = Config.packageName
                        action = Config.bridgeAction
                    },
                    serviceConnection,
                    Context.BIND_AUTO_CREATE,
                    userHandle
                )
                HiddenApiBypass.clearHiddenApiExemptions()
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
        }.onFailure { LogUtil.d(it) }
    }

    fun unbindBridgeService(context: Context? = mContext) {
        LogUtil.d("unbind bridge service")
        runCatching {
            context?.unbindService(serviceConnection)
        }
    }

}