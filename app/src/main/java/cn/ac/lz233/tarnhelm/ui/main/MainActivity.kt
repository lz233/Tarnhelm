package cn.ac.lz233.tarnhelm.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityMainBinding
import cn.ac.lz233.tarnhelm.databinding.DialogAboutBinding
import cn.ac.lz233.tarnhelm.logic.dao.ConfigDao
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.service.ClipboardService
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.extensions.ExtensionsActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity
import cn.ac.lz233.tarnhelm.ui.settings.SettingsActivity
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.toHtml
import cn.ac.lz233.tarnhelm.util.ktx.toString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val workModeList: List<String>
        get() = mutableListOf<String>().apply {
            if (App.isEditTextMenuActive()) add(R.string.mainStatusWorkModeEditTextMenu.getString())
            if (App.isCopyMenuActive()) add(R.string.mainStatusWorkModeCopyMenu.getString())
            if (App.isShareActive()) add(R.string.mainStatusWorkModeShare.getString())
            if (App.isBackgroundMonitoringActive()) add(R.string.mainStatusBackgroundMonitoring.getString())
            if (App.isXposedActive()) add(R.string.mainStatusWorkModeXposed.getString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        init()

        binding.rulesCardView.setOnClickListener { RulesActivity.actionStart(this) }
        binding.extensionsCardView.setOnClickListener { ExtensionsActivity.actionStart(this) }
        binding.settingsCardView.setOnClickListener { SettingsActivity.actionStart(this) }
        binding.shareCardView.setOnClickListener {
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, R.string.app_name.getString())
                putExtra(
                    Intent.EXTRA_TEXT, when (BuildConfig.FLAVOR) {
                        "google" -> "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                        "fdroid" -> "https://f-droid.org/packages/${BuildConfig.APPLICATION_ID}"
                        else -> "https://github.com/lz233/Tarnhelm"
                    }
                )
                type = "text/plain"
            }, R.string.app_name.getString()))
        }
        binding.aboutCardView.setOnClickListener {
            val dialogBinding = DialogAboutBinding.inflate(layoutInflater)
            dialogBinding.versionNameTextView.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ${BuildConfig.FLAVOR} ${BuildConfig.BUILD_TYPE}"
            dialogBinding.sourceCodeTextView.movementMethod = LinkMovementMethod.getInstance()
            dialogBinding.sourceCodeTextView.text = getString(R.string.aboutViewSourceCode, "<b><a href='https://github.com/lz233/Tarnhelm'>GitHub</a></b>").toHtml()
            MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.root)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            if (workModeList.isEmpty()) {
                binding.statusErrorCardView.visibility = View.VISIBLE
                binding.statusPassCardView.visibility = View.GONE
            } else {
                binding.statusErrorCardView.visibility = View.GONE
                binding.statusPassCardView.visibility = View.VISIBLE
                binding.statusPassSummaryTextView.text =
                    getString(R.string.mainStatusPassSummary, workModeList.toString(R.string.mainStatusPunctuation.getString(), R.string.mainStatusPunctuationLast.getString()))
            }
            binding.rulesSummaryTextView.text = getString(R.string.mainRulesSummary, (App.regexRuleDao.getCount() + App.parameterRuleDao.getCount() + App.redirectRuleDao.getCount()).toString())
        }
        if (SettingsDao.workModeBackgroundMonitoring) {
            if (SettingsDao.useForegroundServiceOnBackgroundMonitoring) {
                startForegroundService(Intent(App.context, ClipboardService::class.java))
            } else {
                startService(Intent(App.context, ClipboardService::class.java))
            }
        }
    }

    private fun init() {
        showPrivacyPolicyDialog()
        showRankDialog()
    }

    private fun showRankDialog() {
        ConfigDao.openTimes++
        //if (true){
        if ((ConfigDao.openTimes >= 10) and (!ConfigDao.ranked) and ((0..100).random() >= 50)) {
            ConfigDao.ranked = true
            Snackbar.make(binding.root, R.string.mainRankToast, Toast.LENGTH_SHORT)
                .setAction(R.string.mainRankToastAction) {
                    startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    })
                }.show()
        }
    }

    private fun showPrivacyPolicyDialog() {
        if (!ConfigDao.agreedPrivacyPolicy && BuildConfig.FLAVOR == "coolapk") {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.mainPrivacyPolicyDialogTitle)
                .setMessage(R.string.mainPrivacyPolicyDialogContent)
                .setCancelable(false)
                .setPositiveButton(R.string.mainPrivacyPolicyDialogPositiveButton) { _, _ ->
                    ConfigDao.agreedPrivacyPolicy = true
                    checkPermissions()
                }
                .setNegativeButton(R.string.mainPrivacyPolicyDialogNegativeButton) { _, _ ->
                    exitProcess(0)
                }
                .show()
        } else {
            ConfigDao.agreedPrivacyPolicy = true
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions(Manifest.permission.POST_NOTIFICATIONS)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted or (Build.VERSION.SDK_INT < 33)) {
                    initNotificationChannel()
                } else {
                    Snackbar.make(binding.root, R.string.mainRequestPermissionFailedToast, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initNotificationChannel() {
        App.notificationManager.createNotificationChannels(listOf(
            NotificationChannel("233", R.string.clipboard_service_channel_name.getString(), NotificationManager.IMPORTANCE_LOW).apply {
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setAllowBubbles(false)
            },
            NotificationChannel("234", R.string.process_result_channel_name.getString(), NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setAllowBubbles(false)
            }
        ))
    }
}