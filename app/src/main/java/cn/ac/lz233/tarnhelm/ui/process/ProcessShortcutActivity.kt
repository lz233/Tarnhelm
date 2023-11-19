package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.os.Build
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessShortcutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            launch {
                delay(50)
                copyToClipboard()
                finish()
            }
        } else {
            copyToClipboard()
            finish()
        }
    }

    private fun copyToClipboard() {
        App.clipboardManager.primaryClip?.getItemAt(0)?.text?.doTarnhelms { success, result ->
            if (success) App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", result))
        }
    }
}