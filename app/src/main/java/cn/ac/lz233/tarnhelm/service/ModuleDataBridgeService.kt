package cn.ac.lz233.tarnhelm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge
import kotlin.concurrent.thread

class ModuleDataBridgeService : Service() {

    private val binder = object : ModuleDataBridge.Stub() {

        override fun doTarnhelms(string: String): String {
            var v = ""
            thread {
                v = string.doTarnhelms()
            }.join()
            return v
        }

        override fun ping(): Int = 1
    }

    override fun onBind(p0: Intent): IBinder {
        return binder
    }

}