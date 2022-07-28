package cn.ac.lz233.tarnhelm.ui.process

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms

class ProcessCopyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        App.clipboard.setPrimaryClip(ClipData.newPlainText("Tarnhelm", text.doTarnhelms()))
        finish()
    }
}