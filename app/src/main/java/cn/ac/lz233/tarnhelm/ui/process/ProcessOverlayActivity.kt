package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms

class ProcessOverlayActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.clipboardManager.primaryClip?.getItemAt(0)?.text?.doTarnhelms { success, result ->
            if (success) App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", result))
        }
        finish()
    }
}