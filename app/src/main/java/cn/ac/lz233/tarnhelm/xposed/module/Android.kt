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
            }.onFailure { LogUtil.d(it) }
        }
    }
    
    fun init() {
        runCatching {
            LogUtil.d("start find methods")
            val methods = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.filter { it.name == "startProcessLocked" }
            methods.forEach {
                LogUtil.d(it)
            }
        }.onFailure { LogUtil.d(it) }
        try {
            val systemReadyMethod = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.first { it.name == "systemReady" }
            systemReadyMethod.hookAfterMethod { param ->
                runCatching {
                    activityManagerService = param.thisObject
                    mContext = param.thisObject.getObjectField("mContext") as Context
                    ModuleBridgeHelper.mContext = mContext
                    mContext?.registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
                }.onFailure { LogUtil.d(it) }
            }

            val clipboardServiceClazz = "com.android.server.clipboard.ClipboardService\$ClipboardImpl".findClass()
            val setClipMethod = clipboardServiceClazz.declaredMethods.first { it.name == "setPrimaryClip" }
            setClipMethod.hookBeforeMethod { param ->
                LogUtil.d("setPrimaryClip")
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
                        ModuleBridgeHelper.bridge?.let { item.setObjectField("mText", it.doTarnhelms(text.toString())) }
                    }
                }.onFailure { LogUtil.d(it) }
            }
        } catch (e: Throwable) {
            LogUtil.d(e)
        }
    }

    @SuppressLint("PrivateApi")
    fun startModuleAppProcess(): Boolean {
        val ams = if (isActivityManagerServiceInit) activityManagerService else throw Exception("starting module app process but AMS isn't init")
        return AMSHelper.startModuleAppProcess(ams)
    }

}