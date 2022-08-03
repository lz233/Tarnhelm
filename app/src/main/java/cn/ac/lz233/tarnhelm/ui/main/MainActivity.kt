package cn.ac.lz233.tarnhelm.ui.main

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityMainBinding
import cn.ac.lz233.tarnhelm.databinding.DialogAboutBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity
import cn.ac.lz233.tarnhelm.ui.settings.SettingsActivity
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.toHtml
import cn.ac.lz233.tarnhelm.util.ktx.toString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val workModeList: List<String>
        get() = mutableListOf<String>().apply {
            if (App.isEditTextMenuActive()) add(R.string.mainStatusWorkModeEditTextMenu.getString())
            if (App.isCopyMenuActive()) add(R.string.mainStatusWorkModeCopyMenu.getString())
            if (App.isShareActive()) add(R.string.mainStatusWorkModeShare.getString())
            if (App.isXposedActive()) add(R.string.mainStatusWorkModeXposed.getString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        AppCenter.start(application, "d6f67bf8-858b-451a-98e9-c2c295474e9a", Analytics::class.java, Crashes::class.java)
        //App.context.startForegroundService(Intent(App.context, ClipboardService::class.java))
        initRules()
        binding.toolbar.subtitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.rulesCardView.setOnClickListener { RulesActivity.actionStart(this) }
        binding.settingsCardView.setOnClickListener { SettingsActivity.actionStart(this) }
        binding.aboutCardView.setOnClickListener {
            val dialogBinding = DialogAboutBinding.inflate(layoutInflater)
            dialogBinding.versionNameTextView.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ${BuildConfig.BUILD_TYPE}"
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
            binding.rulesSummaryTextView.text = getString(R.string.mainRulesSummary, (App.regexRuleDao.getCount() + App.parameterRuleDao.getCount()).toString())
        }
    }

    private fun initRules() {
        if (App.sp.getBoolean("isFirstTestRun", true)) {
            App.parameterRuleDao.insert(
                ParameterRule(
                    1,
                    "微信读书",
                    "weread.qq.com",
                    0,
                    JSONArray().apply {
                        put("v")
                    }.toString(),
                    "lz233",
                    1,
                    true
                ),
                ParameterRule(
                    2,
                    "网易云音乐",
                    "y.music.163.com",
                    0,
                    JSONArray().apply {
                        put("id")
                    }.toString(),
                    "lz233",
                    1,
                    true
                ),
                ParameterRule(
                    3,
                    "淘宝/闲鱼",
                    "m.tb.cn",
                    0,
                    JSONArray().apply { put("") }.toString(),
                    "lz233",
                    1,
                    true
                )
            )
            App.regexRuleDao.insert(
                RegexRule(
                    1,
                    "Twitter",
                    JSONArray().apply {
                        put("twitter.com")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("vxtwitter.com")
                        put("")
                    }.toString(),
                    "lz233",
                    1,
                    true
                ),
                RegexRule(
                    2,
                    "酷安",
                    JSONArray().apply {
                        put("coolapk.com/feed/")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("coolapk1s.com/feed/")
                        put("")
                    }.toString(),
                    "lz233",
                    1,
                    true
                ),
                RegexRule(
                    3,
                    "京东",
                    JSONArray().apply {
                        put("item.m.jd.com")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("item.jd.com")
                        put("")
                    }.toString(),
                    "lz233",
                    1,
                    true
                )
            )
            App.editor.putBoolean("isFirstTestRun", false)
        }
    }
}