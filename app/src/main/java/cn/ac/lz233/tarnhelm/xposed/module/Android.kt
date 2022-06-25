package cn.ac.lz233.tarnhelm.xposed.module

import android.annotation.SuppressLint
import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Build
import android.os.DeadObjectException
import android.os.IBinder
import android.os.UserHandle
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge
import cn.ac.lz233.tarnhelm.xposed.util.*
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Field

@SuppressLint("StaticFieldLeak")
object Android {

    private lateinit var activityManagerService: Any
    private val isActivityManagerServiceInit: Boolean
        get() = ::activityManagerService.isInitialized
    private var mContext: Context? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName, p1: IBinder) {
            LogUtil._d("Service connected")
            runCatching {
                val service = ModuleDataBridge.Stub.asInterface(p1)
                LogUtil._d(service.moduleConfig)
            }.onFailure {
                if (it is DeadObjectException) {
                    LogUtil._d("Service may be unusable, renew!")
                    unbindBridgeService()
                    if (startModuleAppProcess()) {
                        bindBridgeService()
                    }
                }
                LogUtil._d(it)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName) {
            LogUtil._d("Service disconnected")
            LogUtil._d("service is down, start service again")
            runCatching {
                if (startModuleAppProcess()) {
                    bindBridgeService()
                }
            }.onFailure { LogUtil._d(it) }
        }
    }

    private val onUserPresentReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            runCatching {
                LogUtil._d("on user present!")
                if (startModuleAppProcess()) {
                    bindBridgeService()
                }
            }.onFailure { LogUtil._d(it) }
        }
    }

    fun init() {
        try {
            val systemReadyMethod = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.first { it.name == "systemReady" }
            systemReadyMethod.hookAfterMethod { param ->
                activityManagerService = param.thisObject
                mContext = param.thisObject.getObjectField("mContext") as Context
                mContext?.registerReceiver(onUserPresentReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
            }
        } catch (e: Throwable) {
            LogUtil._d(e)
        }
    }

    @SuppressLint("PrivateApi")
    private fun startModuleAppProcess(): Boolean {
        val ams = if (isActivityManagerServiceInit) activityManagerService else throw Exception("starting module app process but AMS isn't init")
        return AMSHelper.startModuleAppProcess(ams)
    }

    @SuppressLint("MissingPermission")
    private fun bindBridgeService() {
        LogUtil._d("bind bridge service")
        runCatching {
            val userHandle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                HiddenApiBypass.getStaticFields(UserHandle::class.java).first { field -> (field as Field).name == "CURRENT" } as Field
            } else {
                UserHandle::class.java.getDeclaredField("CURRENT")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mContext?.bindServiceAsUser(
                    Intent().apply {
                        `package` = Config.packageName
                        action = "cn.ac.lz233.tarnhelm.bridge"
                    },
                    serviceConnection,
                    BIND_AUTO_CREATE,
                    userHandle.get(null) as UserHandle
                )
            } else {
                mContext?.bindService(
                    Intent().apply {
                        `package` = Config.packageName
                        action = "cn.ac.lz233.tarnhelm.bridge"
                    },
                    serviceConnection,
                    BIND_AUTO_CREATE
                )
            }
        }.onFailure { LogUtil._d(it) }
    }

    private fun unbindBridgeService() {
        LogUtil._d("unbind bridge service")
        runCatching {
            mContext?.unbindService(serviceConnection)
        }.onFailure { LogUtil._d(it) }
    }

}