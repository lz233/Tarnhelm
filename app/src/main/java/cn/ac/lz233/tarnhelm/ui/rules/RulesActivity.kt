package cn.ac.lz233.tarnhelm.ui.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityRulesBinding
import cn.ac.lz233.tarnhelm.databinding.DialogRegexRuleAddBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.parameter.ParameterRulesFragment
import cn.ac.lz233.tarnhelm.ui.rules.regex.RegexRulesFragment
import cn.ac.lz233.tarnhelm.util.ktx.decodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.toJSONArray
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject

class RulesActivity : BaseActivity() {

    private val binding by lazy { ActivityRulesBinding.inflate(layoutInflater) }
    private val parameterRulesFragment = ParameterRulesFragment()
    private val regexRulesFragment = RegexRulesFragment()
    private val fragments = listOf(parameterRulesFragment, regexRulesFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragments[position]
            override fun getItemCount() = fragments.size
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tabItem, position ->
            when (position) {
                0 -> tabItem.text = R.string.rulesParametersTitle.getString()
                1 -> tabItem.text = R.string.rulesRegexesTitle.getString()
            }
        }.attach()

        binding.addFab.setOnClickListener {
            val dialogBinding = DialogRegexRuleAddBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.regexRulesDialogPositiveButton) { _, _ ->
                    val item = RegexRule(
                        App.regexRuleDao.getMaxId() + 1,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.regexEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.replacementEditText.text.toString().toJSONArray().toString(),
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
                    val ruleJSONObject = JSONObject(App.clipboard.primaryClip!!.getItemAt(0).text.toString().decodeBase64())
                    val item = RegexRule(
                        App.regexRuleDao.getMaxId() + 1,
                        ruleJSONObject.getString("a"),
                        ruleJSONObject.getJSONArray("b").toString(),
                        ruleJSONObject.getJSONArray("c").toString(),
                        ruleJSONObject.getString("d"),
                        1,
                        true
                    )
                    App.regexRuleDao.insert(item)
                    regexRulesFragment.rulesList.add(item)
                    regexRulesFragment.adapter.notifyItemInserted(regexRulesFragment.adapter.itemCount - 1)
                    dialog.dismiss()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, RulesActivity::class.java))
    }
}