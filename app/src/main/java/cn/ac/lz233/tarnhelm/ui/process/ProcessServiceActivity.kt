package cn.ac.lz233.tarnhelm.ui.process

import android.content.Intent
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.service.ClipboardService
import cn.ac.lz233.tarnhelm.ui.BaseActivity

class ProcessServiceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SettingsDao.workModeBackgroundMonitoring) {
            if (SettingsDao.useForegroundServiceOnBackgroundMonitoring)
                startForegroundService(Intent(App.context, ClipboardService::class.java))
            else
                startService(Intent(App.context, ClipboardService::class.java))
        }
        finish()
    }
}