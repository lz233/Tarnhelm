package cn.ac.lz233.tarnhelm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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

        if (shouldExit) {
            // On Android 15, if a app is started since force stop, the ACTION_BOOT_COMPLETED broadcast will be delivered to the app again.
            // https://developer.android.com/about/versions/15/behavior-changes-all#enhanced-stop-states
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM)
                App.activityManager.killBackgroundProcesses(context.packageName)
            else
                exitProcess(0)
        }
    }
}