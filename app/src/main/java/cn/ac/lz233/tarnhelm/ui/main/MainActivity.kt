package cn.ac.lz233.tarnhelm.ui.main

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityMainBinding
import cn.ac.lz233.tarnhelm.databinding.DialogAboutBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity
import cn.ac.lz233.tarnhelm.ui.settings.SettingsActivity
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.toHtml
import cn.ac.lz233.tarnhelm.util.ktx.toString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val workModeList: List<String>
        get() = mutableListOf<String>().apply {
            if (App.isEditTextMenuActive()) add(R.string.mainStatusWorkModeEditTextMenu.getString())
            if (App.isShareActive()) add(R.string.mainStatusWorkModeShare.getString())
            if (App.isXposedActive()) add(R.string.mainStatusWorkModeXposed.getString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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
            binding.rulesSummaryTextView.text = getString(R.string.mainRulesSummary, App.ruleDao.getCount().toString())
        }
    }

    fun initRules() {
        if (App.sp.getBoolean("isFirstTestRun", true)) {
            App.ruleDao.insert(
                Rule(
                    1,
                    "微信读书",
                    JSONArray().apply {
                        put("weread.qq.com")
                        put("""(type|senderVid|wtheme|wfrom|wvid)=.+?&""")
                        put("""&scene=.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("weread.qq.com")
                        put("")
                        put("")
                    }.toString(),
                    "lz233",
                    2,
                    true
                )
            )
            App.ruleDao.insert(
                Rule(
                    2,
                    "网易云音乐",
                    JSONArray().apply {
                        put("y.music.163.com")
                        put("""(uct|dlt|app_version|sc)=.*?&""")
                        put("""&tn=.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("y.music.163.com")
                        put("")
                        put("")
                    }.toString(),
                    "lz233",
                    2,
                    true
                )
            )
            App.ruleDao.insert(
                Rule(
                    3,
                    "淘宝/闲鱼",
                    JSONArray().apply {
                        put("m.tb.cn")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("m.tb.cn")
                        put("")
                    }.toString(),
                    "lz233",
                    2,
                    true
                )
            )
            App.ruleDao.insert(
                Rule(
                    4,
                    "京东",
                    JSONArray().apply {
                        put("item.m.jd.com")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("item.m.jd.com")
                        put("")
                    }.toString(),
                    "lz233",
                    2,
                    true
                )
            )
            App.ruleDao.insert(
                Rule(
                    5,
                    "酷安",
                    JSONArray().apply {
                        put("coolapk.com")
                        put("""\?.*""")
                    }.toString(),
                    JSONArray().apply {
                        put("coolapk1s.com")
                        put("")
                    }.toString(),
                    "lz233",
                    2,
                    true
                )
            )
            App.editor.putBoolean("isFirstTestRun", false)
        }
    }
}