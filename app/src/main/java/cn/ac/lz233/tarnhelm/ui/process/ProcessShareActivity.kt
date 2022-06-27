package cn.ac.lz233.tarnhelm.ui.process

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms

class ProcessShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, (intent.getStringExtra(Intent.EXTRA_SUBJECT)?.doTarnhelms()))
            putExtra(Intent.EXTRA_TEXT, (intent.getStringExtra(Intent.EXTRA_TEXT)?.doTarnhelms()))
            //putExtra(Intent.EXTRA_TITLE, (intent.getStringExtra(Intent.EXTRA_TITLE)?.doTarnhelms() ?: ""))
            type = "text/plain"
        }, null).apply {
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, ComponentName(App.context, ProcessShareActivity::class.java))
        })
        finish()
    }
}