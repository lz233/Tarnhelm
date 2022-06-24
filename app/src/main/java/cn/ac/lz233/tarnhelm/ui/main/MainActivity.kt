package cn.ac.lz233.tarnhelm.ui.main

import android.os.Bundle
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.ActivityMainBinding
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity
import cn.ac.lz233.tarnhelm.ui.settings.SettingsActivity
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.rulesCardView.setOnClickListener { RulesActivity.actionStart(this) }
        binding.settingsCardView.setOnClickListener { SettingsActivity.actionStart(this) }
    }

    override fun onResume() {
        super.onResume()
        launch {
            binding.rulesSummaryTextView.text = "has ${App.ruleDao.getCount()} rule(s)"
        }
    }
}