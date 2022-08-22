package cn.ac.lz233.tarnhelm.service

import android.app.*
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.main.MainActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import java.io.BufferedReader
import java.io.InputStreamReader

class ClipboardService : Service() {

    private var lastText = ""
    private var readerID = 0L

    override fun onCreate() {
        super.onCreate()
        LogUtil._d("ClipboardService onCreate SDK_INT=${Build.VERSION.SDK_INT}")
        App.clipboard.addPrimaryClipChangedListener { doClipboard() }
        magic()
    }

    // From Android 10, Google limited the access to clipboard data in background
    // https://developer.android.com/about/versions/10/privacy/changes#clipboard-data
    private fun magic() {
        val nanoTime = System.nanoTime()
        val handler = Handler(this.mainLooper)
        readerID = nanoTime
        Thread {
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
        }.start()
    }

    private fun doClipboard() {
        App.clipboard.primaryClip?.getItemAt(0)?.text?.let {
            if (lastText != it) {
                lastText = it.doTarnhelms()
                App.clipboard.setPrimaryClip(ClipData.newPlainText("Tarnhelm", lastText))
            }
        }
    }

    private fun createNotification() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("233", "test", NotificationManager.IMPORTANCE_LOW).apply {
            description = "description"
        }
        notificationManager.createNotificationChannel(notificationChannel)

        val notification: Notification = Notification.Builder(this, "233")
            .setContentTitle("title")
            .setContentText("text")
            .setSmallIcon(R.drawable.ic_icon)
            .setContentIntent(pendingIntent)
            .setTicker("ticker")
            .build()
        startForeground(233, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}