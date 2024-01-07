package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms

class ProcessCopyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        text?.doTarnhelms { success, result ->
            App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", result))
        }
        finish()
    }
}