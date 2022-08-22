package cn.ac.lz233.tarnhelm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.service.ClipboardService

class BootBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> if (App.spSettings.getBoolean("workModeBackgroundMonitoring", false)) {
                context.startService(Intent(App.context, ClipboardService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}