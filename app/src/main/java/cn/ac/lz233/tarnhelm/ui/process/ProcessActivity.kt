package cn.ac.lz233.tarnhelm.ui.process

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms

class ProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.action) {
            Intent.ACTION_SEND -> {
                startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, (intent.getStringExtra(Intent.EXTRA_TEXT)?.doTarnhelms() ?: ""))
                    type = "text/plain"
                }, null).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
            }
            Intent.ACTION_PROCESS_TEXT -> {
                val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
                if (readOnly)
                    LogUtil.toast("Read Only")
                else
                    setResult(RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_PROCESS_TEXT, text.doTarnhelms()) })
            }
        }
        finish()
    }
}