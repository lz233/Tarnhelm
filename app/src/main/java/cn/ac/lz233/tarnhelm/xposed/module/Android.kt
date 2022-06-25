package cn.ac.lz233.tarnhelm.xposed.module

import android.annotation.SuppressLint
import android.content.*
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.util.*

@SuppressLint("StaticFieldLeak")
object Android {

    private lateinit var activityManagerService: Any
    private val isActivityManagerServiceInit: Boolean
        get() = ::activityManagerService.isInitialized
    private var mContext: Context? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            runCatching {
                startModuleAppProcess()
                ModuleBridgeHelper.bindBridgeService()
                mContext?.unregisterReceiver(this)
            }.onFailure { LogUtil._d(it) }
        }
    }

    fun init() {
        try {
            val systemReadyMethod = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.first { it.name == "systemReady" }
            systemReadyMethod.hookAfterMethod { param ->
                runCatching {
                    activityManagerService = param.thisObject
                    mContext = param.thisObject.getObjectField("mContext") as Context
                    ModuleBridgeHelper.mContext = mContext
                    mContext?.registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
                }.onFailure { LogUtil._d(it) }
            }
            "com.android.server.clipboard.ClipboardService".hookBeforeMethod("setPrimaryClipInternal", ClipData::class.java, Int::class.java) { param ->
                runCatching {
                    val data = param.args[0] as ClipData? ?: return@hookBeforeMethod
                    if (data.itemCount == 0) return@hookBeforeMethod
                    for (i in 0 until data.itemCount) {
                        val item = data.getItemAt(i)
                        val text = item.getObjectField("mText") as CharSequence? ?: continue
                        if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                            startModuleAppProcess()
                            ModuleBridgeHelper.bindBridgeService()
                        }
                        ModuleBridgeHelper.bridge?.let { item.setObjectField("mText", it.doTarnhelm(text.toString())) }
                    }
                }.onFailure { LogUtil._d(it) }
            }
        } catch (e: Throwable) {
            LogUtil._d(e)
        }
    }

    @SuppressLint("PrivateApi")
    fun startModuleAppProcess(): Boolean {
        val ams = if (isActivityManagerServiceInit) activityManagerService else throw Exception("starting module app process but AMS isn't init")
        return AMSHelper.startModuleAppProcess(ams)
    }

}