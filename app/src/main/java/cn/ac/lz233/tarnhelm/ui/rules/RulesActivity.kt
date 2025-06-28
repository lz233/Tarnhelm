package cn.ac.lz233.tarnhelm.ui.rules

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityRulesBinding
import cn.ac.lz233.tarnhelm.databinding.DialogParameterRuleAddBinding
import cn.ac.lz233.tarnhelm.databinding.DialogRedirectRuleAddBinding
import cn.ac.lz233.tarnhelm.databinding.DialogRegexRuleAddBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RedirectRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import cn.ac.lz233.tarnhelm.ui.SecondaryBaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.parameter.ParameterRulesFragment
import cn.ac.lz233.tarnhelm.ui.rules.redirect.RedirectRulesFragment
import cn.ac.lz233.tarnhelm.ui.rules.regex.RegexRulesFragment
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.decodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.decodeURL
import cn.ac.lz233.tarnhelm.util.ktx.getModeId
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.insertToParameterRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRedirectRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRegexRules
import cn.ac.lz233.tarnhelm.util.ktx.toJSONArray
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject

class RulesActivity : SecondaryBaseActivity() {

    private val binding by lazy { ActivityRulesBinding.inflate(layoutInflater) }
    private val parameterRulesFragment = ParameterRulesFragment()
    private val regexRulesFragment = RegexRulesFragment()
    private val redirectRulesFragment = RedirectRulesFragment()
    private val fragments = listOf(parameterRulesFragment, regexRulesFragment, redirectRulesFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragments[position]
            override fun getItemCount() = fragments.size
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tabItem, position ->
            when (position) {
                0 -> tabItem.text = R.string.rulesParametersTitle.getString()
                1 -> tabItem.text = R.string.rulesRegexesTitle.getString()
                2 -> tabItem.text = R.string.rulesRedirectsTitle.getString()
            }
        }.attach()

        binding.openWebImageView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://tarnhelm.project.ac.cn/rules.html")))
        }

        binding.addFab.setOnClickListener {
            when (binding.viewPager2.currentItem) {
                0 -> {
                    val dialogBinding = DialogParameterRuleAddBinding.inflate(layoutInflater)
                    val dialog = MaterialAlertDialogBuilder(this)
                        .setView(dialogBinding.root)
                        .setPositiveButton(R.string.parameterRulesDialogPositiveButton) { _, _ ->
                            val item = ParameterRule(
                                App.parameterRuleDao.getMaxId() + 1,
                                dialogBinding.descriptionEditText.text.toString(),
                                dialogBinding.domainEditText.text.toString(),
                                dialogBinding.modeToggleButton.checkedButtonId.getModeId(),
                                dialogBinding.parametersEditText.text.toString().toJSONArray().toString(),
                                dialogBinding.authorEditText.text.toString(),
                                0,
                                true
                            )
                            App.parameterRuleDao.insert(item)
                            parameterRulesFragment.rulesList.add(item)
                            parameterRulesFragment.adapter.notifyItemInserted(parameterRulesFragment.adapter.itemCount - 1)
                        }
                        .show()
                    dialogBinding.pasteImageView.setOnClickListener {
                        try {
                            val ruleJSONObject = JSONObject(App.clipboardManager.primaryClip!!.getItemAt(0).text.toString().replace("tarnhelm://rule?parameter=", "").decodeURL().decodeBase64())
                            val item = ruleJSONObject.insertToParameterRules()
                            parameterRulesFragment.rulesList.add(item)
                            parameterRulesFragment.adapter.notifyItemInserted(parameterRulesFragment.adapter.itemCount - 1)
                        } catch (e: Throwable) {
                            Snackbar.make(binding.root, R.string.rulesPasteFailedToast, Toast.LENGTH_SHORT).show()
                            LogUtil.e(e)
                        } finally {
                            dialog.dismiss()
                        }
                    }
                }

                1 -> {
                    val dialogBinding = DialogRegexRuleAddBinding.inflate(layoutInflater)
                    val dialog = MaterialAlertDialogBuilder(this)
                        .setView(dialogBinding.root)
                        .setPositiveButton(R.string.regexRulesDialogPositiveButton) { _, _ ->
                            val item = RegexRule(
                                App.regexRuleDao.getMaxId() + 1,
                                dialogBinding.descriptionEditText.text.toString(),
                                dialogBinding.regexesEditText.text.toString().toJSONArray().toString(),
                                dialogBinding.replacementsEditText.text.toString().toJSONArray().toString(),
                                dialogBinding.authorEditText.text.toString(),
                                0,
                                true
                            )
                            App.regexRuleDao.insert(item)
                            regexRulesFragment.rulesList.add(item)
                            regexRulesFragment.adapter.notifyItemInserted(regexRulesFragment.adapter.itemCount - 1)
                        }
                        .show()
                    dialogBinding.pasteImageView.setOnClickListener {
                        try {
                            val ruleJSONObject = JSONObject(App.clipboardManager.primaryClip!!.getItemAt(0).text.toString().replace("tarnhelm://rule?regex=", "").decodeURL().decodeBase64())
                            val item = ruleJSONObject.insertToRegexRules()
                            regexRulesFragment.rulesList.add(item)
                            regexRulesFragment.adapter.notifyItemInserted(regexRulesFragment.adapter.itemCount - 1)
                        } catch (e: Throwable) {
                            Snackbar.make(binding.root, R.string.rulesPasteFailedToast, Toast.LENGTH_SHORT).show()
                            LogUtil.e(e)
                        } finally {
                            dialog.dismiss()
                        }
                    }
                }

                2 -> {
                    val dialogBinding = DialogRedirectRuleAddBinding.inflate(layoutInflater)
                    val dialog = MaterialAlertDialogBuilder(this)
                        .setView(dialogBinding.root)
                        .setPositiveButton(R.string.redirectRulesDialogPositiveButton) { _, _ ->
                            val item = RedirectRule(
                                App.redirectRuleDao.getMaxId() + 1,
                                dialogBinding.descriptionEditText.text.toString(),
                                dialogBinding.domainEditText.text.toString(),
                                dialogBinding.userAgentEditText.text.toString(),
                                dialogBinding.authorEditText.text.toString(),
                                0,
                                true
                            )
                            App.redirectRuleDao.insert(item)
                            redirectRulesFragment.rulesList.add(item)
                            redirectRulesFragment.adapter.notifyItemInserted(redirectRulesFragment.adapter.itemCount - 1)
                        }
                        .show()
                    dialogBinding.pasteImageView.setOnClickListener {
                        try {
                            val ruleJSONObject = JSONObject(App.clipboardManager.primaryClip!!.getItemAt(0).text.toString().replace("tarnhelm://rule?redirect=", "").decodeURL().decodeBase64())
                            val item = ruleJSONObject.insertToRedirectRules()
                            redirectRulesFragment.rulesList.add(item)
                            redirectRulesFragment.adapter.notifyItemInserted(redirectRulesFragment.adapter.itemCount - 1)
                        } catch (e: Throwable) {
                            Snackbar.make(binding.root, R.string.rulesPasteFailedToast, Toast.LENGTH_SHORT).show()
                            LogUtil.e(e)
                        } finally {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(
            Intent(context, RulesActivity::class.java)
        )
    }
}