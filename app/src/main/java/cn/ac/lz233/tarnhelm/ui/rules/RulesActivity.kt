package cn.ac.lz233.tarnhelm.ui.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.ActivityRulesBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import org.json.JSONArray

class RulesActivity : BaseActivity() {

    private val binding by lazy { ActivityRulesBinding.inflate(layoutInflater) }
    private val rulesList by lazy { App.ruleDao.getAll() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        App.ruleDao.insert(
            Rule(
                1,
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
        binding.rulesRecyclerView.adapter = RulesAdapter(rulesList)
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, RulesActivity::class.java))
    }
}