package cn.ac.lz233.tarnhelm.ui.process

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelm

class ProcessAvtivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        if (readOnly) {
            LogUtil.toast("Read Only")
        } else {
            val result = text.doTarnhelm()
            setResult(RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_PROCESS_TEXT, result) })
            //LogUtil.toast(result)
        }
        finish()
    }
}