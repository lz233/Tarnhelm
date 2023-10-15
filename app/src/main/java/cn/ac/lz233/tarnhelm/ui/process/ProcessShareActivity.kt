package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessShareActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)

        // must have a delay because Android uses a very fool method to detect if app is in focus
        if (Build.VERSION.SDK_INT >= 33) {
            launch {
                delay(50)
                if (App.clipboardManager.primaryClip?.getItemAt(0)?.text == intent.getStringExtra(Intent.EXTRA_TEXT))
                    copyToClipboard()
                else
                    startChooser()
                finish()
            }
        } else {
            startChooser()
            finish()
        }

        //finish()
    }

    private fun startChooser() {
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, (intent.getStringExtra(Intent.EXTRA_SUBJECT)?.doTarnhelms()))
            putExtra(Intent.EXTRA_TEXT, (intent.getStringExtra(Intent.EXTRA_TEXT)?.doTarnhelms()))
            //putExtra(Intent.EXTRA_TITLE, (intent.getStringExtra(Intent.EXTRA_TITLE)?.doTarnhelms() ?: ""))
            type = intent.type
        }, null).apply {
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(ComponentName(App.context, ProcessShareActivity::class.java)))
        })
    }

    private fun copyToClipboard() {
        App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", App.clipboardManager.primaryClip?.getItemAt(0)?.text?.doTarnhelms()))
    }
}