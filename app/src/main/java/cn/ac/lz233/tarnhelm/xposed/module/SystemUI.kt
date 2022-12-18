package cn.ac.lz233.tarnhelm.xposed.module

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.RemoteAction
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.core.view.size
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.main.MainActivity
import cn.ac.lz233.tarnhelm.ui.process.ProcessOverlayActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.xposed.Config
import cn.ac.lz233.tarnhelm.xposed.util.*

object SystemUI {
    private var text1: CharSequence = ""
    private var text2: CharSequence = ""

    @SuppressLint("ResourceType")
    fun init() {
        runCatching {
            "com.android.systemui.clipboardoverlay.ClipboardOverlayController".hookAfterMethod(
                "setClipData",
                ClipData::class.java,
                String::class.java
            ) {
                LogUtil.xpd("setClipData")
                if (!Config.sp.getBoolean("overrideClipboardOverlay", false)) return@hookAfterMethod

                val clipboardOverlayController = it.thisObject
                val context = clipboardOverlayController.getObjectField("mContext") as Context
                //val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val actionContainer = clipboardOverlayController.getObjectField("mActionContainer") as LinearLayout
                // Android may call setClipData twice
                if ((actionContainer[actionContainer.size - 1] as View).contentDescription != "Tarnhelm") {
                    val chip = it.thisObject.callMethod(
                        "constructActionChip",
                        RemoteAction(
                            Icon.createWithResource(BuildConfig.APPLICATION_ID, R.drawable.ic_icon),
                            "",
                            "Tarnhelm",
                            PendingIntent.getActivity(context, 1, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
                        )
                    ) as View
                    chip.contentDescription = "Tarnhelm"
                    chip.setOnClickListener {
                        clipboardOverlayController.callMethod("animateOut")
                        context.startActivity(Intent().setClassName(BuildConfig.APPLICATION_ID, ProcessOverlayActivity::class.java.name).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    actionContainer.addView(chip)
                }
            }
        }.onFailure { LogUtil.xpe(it) }
    }

}