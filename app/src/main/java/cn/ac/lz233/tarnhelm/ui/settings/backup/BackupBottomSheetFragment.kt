package cn.ac.lz233.tarnhelm.ui.settings.backup

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.service.chooser.ChooserAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.FragmentBackupBinding
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.listFilesRecursively
import cn.ac.lz233.tarnhelm.util.ktx.unzip
import cn.ac.lz233.tarnhelm.util.ktx.useFlymeChooser
import cn.ac.lz233.tarnhelm.util.ktx.zip
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

class BackupBottomSheetFragment : BottomSheetDialogFragment(), CoroutineScope by MainScope() {

    private val binding by lazy { FragmentBackupBinding.inflate(layoutInflater) }
    private val backupDir by lazy {
        listOf(
            File(requireContext().dataDir, "databases"),
            File(requireContext().dataDir, "shared_prefs")
        )
    }
    private val backupFiles by lazy { backupDir.listFilesRecursively() }
    private val outputDir by lazy { File(requireContext().cacheDir, "backup") }
    private val outputFile by lazy { File(outputDir, "tarnhelm-backup-${BuildConfig.VERSION_NAME}-${System.currentTimeMillis()}.zip") }
    private val shareOutputFileIntent by lazy {
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/zip"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TITLE, outputFile.name)
            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.file.provider", outputFile))
        }
    }
    private val shareChooserReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val clickedComponent = intent.getParcelableExtra<ComponentName>(Intent.EXTRA_CHOSEN_COMPONENT);
                LogUtil._d(clickedComponent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && clickedComponent == null) {
                    return
                } else {
                    dismiss()
                }
            }
        }
    }
    private val shareChooserIntentFilter = IntentFilter().apply {
        addAction("${BuildConfig.APPLICATION_ID}.backup.shareCallback")
    }
    private val saveFileViaSAFReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                LogUtil._d("registerReceiver: saveViaSAF")
                App.context.startActivity(shareOutputFileIntent.apply {
                    `package` = BuildConfig.APPLICATION_ID
                    component = ComponentName(BuildConfig.APPLICATION_ID, SaveViaSAFActivity::class.java.name)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                dismiss()
            }
        }
    }
    private val saveFileViaSAFIntentFilter = IntentFilter().apply {
        addAction("${BuildConfig.APPLICATION_ID}.backup.saveViaSAF")
    }
    private val selectFileCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        LogUtil._d(result.data?.data)
        if (result.resultCode == Activity.RESULT_OK) {
            val fileUri = result.data?.data ?: return@registerForActivityResult
            launch {
                binding.progressIndicator.visibility = View.VISIBLE
                backupDir.forEach { it.deleteRecursively() }
                requireContext().contentResolver.openInputStream(fileUri)?.unzip(requireContext().dataDir)
                exitProcess(0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            outputDir.deleteRecursively()
        }
        ContextCompat.registerReceiver(App.context, shareChooserReceiver, shareChooserIntentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
        ContextCompat.registerReceiver(App.context, saveFileViaSAFReceiver, saveFileViaSAFIntentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.restoreButton.setOnClickListener {
            startRestore()
        }

        binding.backupButton.setOnClickListener {
            LogUtil._d(backupFiles)
            startBackup()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.context.unregisterReceiver(shareChooserReceiver)
        App.context.unregisterReceiver(saveFileViaSAFReceiver)
    }

    private fun startBackup() {
        launch {
            binding.progressIndicator.visibility = View.VISIBLE
            backupFiles.zip(requireContext().dataDir, outputFile)
            binding.progressIndicator.visibility = View.INVISIBLE
            startActivity(Intent.createChooser(
                shareOutputFileIntent, R.string.backupTitle.getString(), PendingIntent.getBroadcast(
                    requireContext(),
                    233,
                    Intent("${BuildConfig.APPLICATION_ID}.backup.shareCallback").apply {
                        `package` = BuildConfig.APPLICATION_ID
                    },
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                ).intentSender
            ).apply {
                useFlymeChooser(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Android 14 supported custom actions.
                    // https://developer.android.com/training/sharing/send#custom-actions
                    putExtra(
                        Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, arrayOf(
                            ChooserAction.Builder(
                                IconCompat.createWithResource(requireContext(), R.drawable.ic_save).toIcon(requireContext()),
                                R.string.backupSaveViaSAFTitle.getString(),
                                PendingIntent.getBroadcast(
                                    requireContext(),
                                    234,
                                    Intent("${BuildConfig.APPLICATION_ID}.backup.saveViaSAF").apply {
                                        `package` = BuildConfig.APPLICATION_ID
                                    },
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                            ).build()
                        )
                    )
                } else {
                    // Pin the custom action instead below Android 14.
                    // https://developer.android.com/training/sharing/send#adding-custom-targets
                    putExtra(
                        Intent.EXTRA_INITIAL_INTENTS, arrayOf(
                            Intent(requireContext(), SaveViaSAFActivity::class.java).apply {
                                `package` = BuildConfig.APPLICATION_ID
                                action = Intent.ACTION_SEND
                                type = "application/zip"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                putExtra(Intent.EXTRA_TITLE, outputFile.name)
                                putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.file.provider", outputFile))
                            }
                        )
                    )
                }
            })
        }
    }

    private fun startRestore() {
        // When using SAF, READ_EXTERNAL_STORAGE is not needed any more from Android Q
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf() else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    val selectFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/zip"
                    }
                    selectFileCallback.launch(selectFileIntent)
                } else {
                    Snackbar.make(binding.root, R.string.backupRequestPermissionFailedToast, Toast.LENGTH_SHORT).show()
                }
            }
    }
}