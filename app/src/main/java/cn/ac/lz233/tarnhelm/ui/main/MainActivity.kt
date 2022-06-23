package cn.ac.lz233.tarnhelm.ui.main

import android.os.Bundle
import androidx.core.view.WindowCompat
import cn.ac.lz233.tarnhelm.databinding.ActivityMainBinding
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.ui.rules.RulesActivity

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.rulesCardView.setOnClickListener { RulesActivity.actionStart(this) }
    }
}