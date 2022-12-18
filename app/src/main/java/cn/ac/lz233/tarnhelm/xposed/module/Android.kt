package cn.ac.lz233.tarnhelm.xposed.module

import android.annotation.SuppressLint
import android.content.*
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import cn.ac.lz233.tarnhelm.xposed.util.*
import kotlin.concurrent.thread

@SuppressLint("StaticFieldLeak")
object Android {

    private lateinit var activityManagerService: Any
    private val isActivityManagerServiceInit: Boolean
        get() = ::activityManagerService.isInitialized
    private var mContext: Context? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            thread {
                runCatching {
                    Thread.sleep(1000)
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                    mContext?.unregisterReceiver(this)
                }.onFailure { LogUtil.xpe(it) }
            }
        }
    }

    fun init() {
        runCatching {
            LogUtil.xp("start find methods")
            val methods = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.filter { it.name == "startProcessLocked" }
            methods.forEach {
                LogUtil.xp(it)
            }
        }.onFailure { LogUtil.xpe(it) }

        try {
            disableBackgroundCheck()
            val systemReadyMethod = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.first { it.name == "systemReady" }
            systemReadyMethod.hookAfterMethod { param ->
                runCatching {
                    activityManagerService = param.thisObject
                    mContext = param.thisObject.getObjectField("mContext") as Context
                    ModuleBridgeHelper.mContext = mContext
                    mContext?.registerReceiver(receiver, IntentFilter(Intent.ACTION_USER_PRESENT))
                }.onFailure { LogUtil.xpe(it) }
            }

            val clipboardServiceClazz = "com.android.server.clipboard.ClipboardService\$ClipboardImpl".findClass()
            val setClipMethod = clipboardServiceClazz.declaredMethods.first { it.name == "setPrimaryClip" }
            setClipMethod.hookBeforeMethod { param ->
                LogUtil.xp("setPrimaryClip")
                if (!Config.sp.getBoolean("rewriteClipboard", true)) return@hookBeforeMethod

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
                        ModuleBridgeHelper.bridge?.let {
                            item.setObjectField("mText", it.doTarnhelms(text.toString()))
                        }
                    }
                }.onFailure { LogUtil.xpe(it) }
            }

            /*Intent::class.java.hookBeforeAllMethods("putExtra"){
                LogUtil.xp(it.method)
                LogUtil.xp("1 ${it.args[0]}")
                LogUtil.xp("2 ${it.args[1]}")
            }
            Intent::class.java.hookBeforeMethod("putExtra", String::class.java, String::class.java) { param ->
                LogUtil.xp("putExtraString ${param.args[0]} ${param.args[1]}")
                if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                }
                /*ModuleBridgeHelper.bridge?.let {
                    param.args[1] = it.doTarnhelms(param.args[1] as String)
                }*/
            }
            Intent::class.java.hookBeforeMethod("putExtra", String::class.java, CharSequence::class.java) { param ->
                LogUtil.xp("putExtraCharSequence ${param.args[0]} ${param.args[1]}")
                if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                }
                /*ModuleBridgeHelper.bridge?.let {
                    param.args[1] = it.doTarnhelms(param.args[1] as String)
                }*/
            }*/
            /*Intent::class.java.hookBeforeAllMethods("createChooser") { param ->
                LogUtil.xp("createChooser")
                val target = param.args[0] as Intent
                if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                }
                ModuleBridgeHelper.bridge?.let {
                    target.putExtra(Intent.EXTRA_SUBJECT, it.doTarnhelms(target.getStringExtra(Intent.EXTRA_SUBJECT)))
                    target.putExtra(Intent.EXTRA_TEXT, it.doTarnhelms(target.getStringExtra(Intent.EXTRA_TEXT)))
                    param.args[0] = target
                }
            }*/
        } catch (e: Throwable) {
            LogUtil.xpe(e)
        }

        "com.android.server.clipboard.ClipboardService".hookBeforeMethod(
            "isDefaultIme",
            Int::class.javaPrimitiveType,
            String::class.java
        ) {
            if ((it.args[1] as String) == BuildConfig.APPLICATION_ID) it.result = true
        }
    }

    private fun disableBackgroundCheck() {
        runCatching {
            "com.android.server.am.UidRecord".hookBeforeMethod("isIdle") { param ->
                runCatching {
                    val ams = param.thisObject.getObjectField("mService") ?: throw Exception("AMS is null")
                    val context = ams.getObjectField("mContext") as Context
                    val uid = param.thisObject.getIntField("mUid")
                    context.packageManager.getPackagesForUid(uid)?.let {
                        if (it.contains(Config.packageName)) {
                            LogUtil.xpe("isIdle hooked, set result to false")
                            param.result = false
                        }
                    }
                }.onFailure { LogUtil.xpe(it) }
            }
        }.onFailure { LogUtil.xpe(it) }
    }

    @SuppressLint("PrivateApi")
    fun startModuleAppProcess(): Boolean {
        val ams = if (isActivityManagerServiceInit) activityManagerService else throw Exception("starting module app process but AMS isn't init")
        return AMSHelper.startModuleAppProcess(ams)
    }

}