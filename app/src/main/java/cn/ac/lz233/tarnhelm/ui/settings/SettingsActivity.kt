package cn.ac.lz233.tarnhelm.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.BaseActivity

class SettingsActivity : BaseActivity() {

    //private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportFragmentManager.beginTransaction()
            .replace(R.id.preferenceFragment, SettingsFragment())
            .commit()
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, SettingsActivity::class.java))
    }
}