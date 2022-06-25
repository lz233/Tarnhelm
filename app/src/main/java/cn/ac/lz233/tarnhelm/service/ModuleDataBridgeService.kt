package cn.ac.lz233.tarnhelm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.ac.lz233.tarnhelm.xposed.ModuleDataBridge

class ModuleDataBridgeService: Service() {

    private val binder = object : ModuleDataBridge.Stub() {
        override fun getModuleConfig(): String {
            return "Hello, world"
        }
    }

    override fun onBind(p0: Intent): IBinder {
        return binder
    }

}