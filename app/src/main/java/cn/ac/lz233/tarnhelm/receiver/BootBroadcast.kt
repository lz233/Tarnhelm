package cn.ac.lz233.tarnhelm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.service.ClipboardService
import kotlin.system.exitProcess

class BootBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var shouldExit = true
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> if (SettingsDao.workModeBackgroundMonitoring) {
                if (SettingsDao.useForegroundServiceOnBackgroundMonitoring) {
                    context.startForegroundService(Intent(App.context, ClipboardService::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } else {
                    context.startService(Intent(App.context, ClipboardService::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
                shouldExit = false
            }
        }
        if (shouldExit) exitProcess(0)
    }
}