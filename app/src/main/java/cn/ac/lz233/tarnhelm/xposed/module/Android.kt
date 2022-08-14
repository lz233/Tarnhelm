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
        runCatching {
            LogUtil._d("start find methods")
            val methods = "com.android.server.am.ActivityManagerService".findClass().declaredMethods.filter { it.name == "startProcessLocked" }
            methods.forEach {
                LogUtil._d(it)
            }
        }.onFailure { LogUtil._d(it) }
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

            val clipboardServiceClazz = "com.android.server.clipboard.ClipboardService\$ClipboardImpl".findClass()
            val setClipMethod = clipboardServiceClazz.declaredMethods.first { it.name == "setPrimaryClip" }
            setClipMethod.hookBeforeMethod { param ->
                LogUtil._d("setPrimaryClip")
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
                }.onFailure { LogUtil._d(it) }
            }

            /*Intent::class.java.hookBeforeAllMethods("putExtra"){
                LogUtil._d(it.method)
                LogUtil._d("1 ${it.args[0]}")
                LogUtil._d("2 ${it.args[1]}")
            }
            Intent::class.java.hookBeforeMethod("putExtra", String::class.java, String::class.java) { param ->
                LogUtil._d("putExtraString ${param.args[0]} ${param.args[1]}")
                if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                }
                /*ModuleBridgeHelper.bridge?.let {
                    param.args[1] = it.doTarnhelms(param.args[1] as String)
                }*/
            }
            Intent::class.java.hookBeforeMethod("putExtra", String::class.java, CharSequence::class.java) { param ->
                LogUtil._d("putExtraCharSequence ${param.args[0]} ${param.args[1]}")
                if (!(ModuleBridgeHelper.isBridgeAvailable && ModuleBridgeHelper.isBridgeActive())) {
                    startModuleAppProcess()
                    ModuleBridgeHelper.bindBridgeService()
                }
                /*ModuleBridgeHelper.bridge?.let {
                    param.args[1] = it.doTarnhelms(param.args[1] as String)
                }*/
            }*/
            /*Intent::class.java.hookBeforeAllMethods("createChooser") { param ->
                LogUtil._d("createChooser")
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
            LogUtil._d(e)
        }
    }

    @SuppressLint("PrivateApi")
    fun startModuleAppProcess(): Boolean {
        val ams = if (isActivityManagerServiceInit) activityManagerService else throw Exception("starting module app process but AMS isn't init")
        return AMSHelper.startModuleAppProcess(ams)
    }

}