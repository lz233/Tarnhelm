package cn.ac.lz233.tarnhelm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelm
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge

class ModuleDataBridgeService : Service() {

    private val binder = object : ModuleDataBridge.Stub() {

        override fun doTarnhelm(s: String): String {
            return s.doTarnhelm().toString()
        }

        override fun ping(): Int = 1
    }

    override fun onBind(p0: Intent): IBinder {
        return binder
    }

}