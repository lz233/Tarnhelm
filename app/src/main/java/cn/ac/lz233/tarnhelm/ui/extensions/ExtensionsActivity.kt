package cn.ac.lz233.tarnhelm.ui.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityExtensionsBinding
import cn.ac.lz233.tarnhelm.extension.ExtensionManager
import cn.ac.lz233.tarnhelm.ui.SecondaryBaseActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.unzip
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import kotlin.system.exitProcess

class ExtensionsActivity : SecondaryBaseActivity() {

    private val binding by lazy { ActivityExtensionsBinding.inflate(layoutInflater) }

    private val onExtInstallExceptionHandler = CoroutineExceptionHandler { _, exception ->
        // TODO: handle different exceptions during extension installation
        Log.e("ExtensionManager", "Failed to install extension", exception)
    }

    private val selectFileCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val fileUri = result.data?.data ?: return@registerForActivityResult

            // check the file extension is .dex
            if (fileUri.path?.endsWith(".dex") == false) {
//                    Snackbar.make(binding.root, R.string.extensionNotSupported, Snackbar.LENGTH_SHORT).show() // TODO
                return@registerForActivityResult
            }

            contentResolver.openInputStream(fileUri)?.let {
                launch(onExtInstallExceptionHandler) {
                    ExtensionManager.installExtension(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(toolbar)



        binding.openWebImageView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://tarnhelm.project.ac.cn/rules.html")))
        }

        binding.importFab.setOnClickListener {
            startImport()
        }

        ExtensionManager.startExtensionConfigurationPanel("cn.ac.lz233.tarnhelm.ext.example", this)
    }

    private fun startImport() {
        // When using SAF, READ_EXTERNAL_STORAGE is not needed any more from Android Q
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf() else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    val selectFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                    selectFileCallback.launch(selectFileIntent)
                } else {
                    Snackbar.make(binding.root, R.string.backupRequestPermissionFailedToast, Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, ExtensionsActivity::class.java))
    }
}