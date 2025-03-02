package cn.ac.lz233.tarnhelm.service

import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.main.MainActivity
import cn.ac.lz233.tarnhelm.ui.process.ProcessServiceActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import cn.ac.lz233.tarnhelm.util.ktx.getString
import rikka.shizuku.Shizuku


class ClipboardService : Service() {
    private var text1: CharSequence = ""
    private var text2: CharSequence = ""
    private val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, ClipboardShizukuService::class.java.name))
        .daemon(false)
        .processNameSuffix("clipboard-shizuku")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null && binder.pingBinder()) {
                val service = IClipboardShizukuService.Stub.asInterface(binder)
                LogUtil._d("ClipboardShizukuService onServiceConnected: $name")
                service.start()
                service.addCallback(object : ShizukuCallback.Stub() {
                    override fun onOpNoted(op: String, uid: Int, packageName: String, attributionTag: String?, flags: Int, result: Int) {
                        if (op == "android:write_clipboard" && packageName != BuildConfig.APPLICATION_ID) {
                            magic()
                        }
                    }
                })
                createNotification()
            } else {
                LogUtil._d("ClipboardShizukuService binder is null or dead")
                createNotification(content = R.string.clipboard_service_permission_needed.getString())
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtil._d("ClipboardShizukuService onServiceDisconnected: $name")
            createNotification(content = R.string.clipboard_service_permission_needed.getString())
        }
    }
    private val binderReceivedListener: () -> Unit = {
        LogUtil._d("binderReceivedListener")
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Shizuku.bindUserService(userServiceArgs, userServiceConnection)
            createNotification()
        } else {
            LogUtil.toast(R.string.clipboard_service_permission_needed.getString())
            createNotification(content = R.string.clipboard_service_permission_needed.getString())
        }
    }
    private val binderDeadListener: () -> Unit = {
        createNotification(content = R.string.clipboard_service_permission_needed.getString())
    }
    private val primaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        doClipboard()
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil._d("ClipboardService onCreate SDK_INT=${Build.VERSION.SDK_INT}")
        // In initial stage, both ReceivedListener and DeadListener will not be called.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createNotification(content = R.string.clipboard_service_permission_needed.getString())
        } else {
            createNotification()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
        } else {
            App.clipboardManager.addPrimaryClipChangedListener(primaryClipChangedListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil._d("ClipboardService onDestroy")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
            if (Shizuku.pingBinder()) Shizuku.unbindUserService(userServiceArgs, userServiceConnection, true)
        } else {
            App.clipboardManager.removePrimaryClipChangedListener(primaryClipChangedListener)
        }
        App.context.startActivity(Intent(App.context, ProcessServiceActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun magic() {
        val handler = Handler(mainLooper)
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

    private fun doClipboard() {
        App.clipboardManager.primaryClip?.getItemAt(0)?.text?.let {
            if (it != text1 && it != text2) {
                LogUtil._d("ClipboardService doClipboard")
                text1 = it
                it.doTarnhelms { success, result ->
                    if (success) {
                        text2 = result
                        App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", text2))
                    }
                }
            }
        }
    }

    private fun createNotification(title: String = R.string.clipboard_service_started.getString(), content: String = R.string.clipboard_service_summary.getString()) {
        val notification = NotificationCompat.Builder(this, "233")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_icon)
            .setColor(getColor(R.color.ic_launcher_background))
            .setContentIntent(Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            })
            .setShowWhen(false)
            .build()
        startForeground(233, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}