package cn.ac.lz233.tarnhelm.ui.settings.backup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream

class SaveViaSAFActivity : BaseActivity() {
    private val selectFileCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val originalFileUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            val outputFileUri = result.data?.data
            if (originalFileUri == null || outputFileUri == null) finish()
            launch {
                val originalStream = BufferedInputStream(contentResolver.openInputStream(originalFileUri!!))
                val outputStream = BufferedOutputStream(contentResolver.openOutputStream(outputFileUri!!))
                originalStream.copyTo(outputStream)
                originalStream.close()
                outputStream.close()
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_SEND) {
            val selectFileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/zip"
                putExtra(Intent.EXTRA_TITLE, intent.getStringExtra(Intent.EXTRA_TITLE))
            }
            selectFileCallback.launch(selectFileIntent)
        } else {
            finish()
        }
    }
}