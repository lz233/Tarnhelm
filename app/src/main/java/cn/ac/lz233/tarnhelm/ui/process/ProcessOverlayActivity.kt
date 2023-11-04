package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import kotlin.concurrent.thread

class ProcessOverlayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        thread{
            App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", App.clipboardManager.primaryClip?.getItemAt(0)?.text?.doTarnhelms()))
            finish()
        }
    }
}