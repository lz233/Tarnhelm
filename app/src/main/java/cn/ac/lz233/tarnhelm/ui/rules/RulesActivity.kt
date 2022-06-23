package cn.ac.lz233.tarnhelm.ui.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.ActivityRulesBinding
import cn.ac.lz233.tarnhelm.databinding.DialogEditBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray

class RulesActivity : BaseActivity() {

    private val binding by lazy { ActivityRulesBinding.inflate(layoutInflater) }
    private val rulesList by lazy { App.ruleDao.getAll() }
    private val adapter by lazy { RulesAdapter(rulesList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        App.ruleDao.insert(
            Rule(
                0,
                "Fuck Coolapk Feed",
                JSONArray().apply {
                    put("""\?.*""")
                    put("coolapk.com")
                }.toString(),
                JSONArray().apply {
                    put("")
                    put("coolapk1s.com")
                }.toString(),
                "lz233"
            )
        )
        binding.rulesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rulesRecyclerView.adapter = adapter
        binding.addFab.setOnClickListener {
            val dialogBinding = DialogEditBinding.inflate(layoutInflater)
            dialogBinding.regexEditText.setText("""[""]""")
            dialogBinding.replacementEditText.setText("""[""]""")
            MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.root)
                .setPositiveButton("OK") { _, _ ->
                    val item = Rule(
                        App.ruleDao.getCount(),
                        dialogBinding.descriptionEditText.text.toString(),
                        JSONArray(dialogBinding.regexEditText.text.toString()).toString(),
                        JSONArray(dialogBinding.replacementEditText.text.toString()).toString(),
                        dialogBinding.authorEditText.text.toString()
                    )
                    App.ruleDao.insert(item)
                    rulesList.add(item)
                    adapter.notifyItemChanged(adapter.itemCount)
                }
                .show()
        }
    }

    fun refreshRulesList() {
        rulesList.clear()
        rulesList.addAll(App.ruleDao.getAll())
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, RulesActivity::class.java))
    }
}