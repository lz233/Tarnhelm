package cn.ac.lz233.tarnhelm.ui.process

import android.content.Intent
import android.os.Bundle
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.doTarnhelms
import cn.ac.lz233.tarnhelm.util.ktx.getString
import kotlin.concurrent.thread

class ProcessEditTextActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        thread {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            if (readOnly) {
                LogUtil.toast(R.string.read_only_toast.getString())
            } else {
                setResult(RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_PROCESS_TEXT, text.doTarnhelms()) })
            }
            finish()
        }
    }
}