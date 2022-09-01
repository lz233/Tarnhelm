package cn.ac.lz233.tarnhelm.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.ui.main.MainActivity
import cn.ac.lz233.tarnhelm.ui.process.ProcessServiceActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import cn.ac.lz233.tarnhelm.util.ktx.getString
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class ClipboardService : Service() {

    private var text1: CharSequence = ""
    private var text2: CharSequence = ""
    private var readerID = 0L
    private val primaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        if (Build.VERSION.SDK_INT < 29) doClipboard()
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil._d("ClipboardService onCreate SDK_INT=${Build.VERSION.SDK_INT}")
        App.clipboardManager.addPrimaryClipChangedListener(primaryClipChangedListener)
        if (Build.VERSION.SDK_INT >= 29 && App.checkClipboardPermission()) magic()
        if (SettingsDao.useForegroundServiceOnBackgroundMonitoring) {
            createNotification()
        } else {
            LogUtil.toast(R.string.clipboard_service_started.getString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil._d("ClipboardService onDestroy")
        App.clipboardManager.removePrimaryClipChangedListener(primaryClipChangedListener)
        App.context.startActivity(Intent(App.context, ProcessServiceActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    // From Android 10, Google limited the access to clipboard data in background
    // https://developer.android.com/about/versions/10/privacy/changes#clipboard-data
    private fun magic() {
        val nanoTime = System.nanoTime()
        val handler = Handler(this.mainLooper)
        readerID = nanoTime
        thread {
            try {
                Runtime.getRuntime().exec("logcat -c").waitFor()
                val exec = Runtime.getRuntime().exec("""logcat ClipboardService:E *:S -T 1""")
                val bufferedReader = BufferedReader(InputStreamReader(exec.inputStream))
                while (readerID == nanoTime) {
                    val readLine = bufferedReader.readLine()
                    if (readLine != null && readLine.contains("Denying clipboard access to ${BuildConfig.APPLICATION_ID}")) {
                        handler.post {
                            val windowManager = getSystemService(WindowManager::class.java) as WindowManager
                            val view = View(applicationContext)
                            windowManager.addView(view, WindowManager.LayoutParams(-2, -2, 2038, 32, -3).apply {
                                x = 0
                                y = 0
                                width = 0
                                height = 0
                            })
                            doClipboard()
                            windowManager.removeView(view)
                        }
                    }
                }
                exec.destroy()
                bufferedReader.close()
            } catch (th: Throwable) {
                LogUtil.e(th)
            }
        }
    }

    private fun doClipboard() {
        LogUtil._d("doClipboard")
        App.clipboardManager.primaryClip?.getItemAt(0)?.text?.let {
            if (it != text1 && it != text2) {
                text1 = it
                text2 = it.doTarnhelms()
                App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", text2))
            }
        }
    }

    private fun createNotification() {
        val notification = Notification.Builder(this, "233")
            .setContentTitle(R.string.clipboard_service_started.getString())
            .setSmallIcon(R.drawable.ic_icon)
            .setContentIntent(Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            })
            .build()
        startForeground(233, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}