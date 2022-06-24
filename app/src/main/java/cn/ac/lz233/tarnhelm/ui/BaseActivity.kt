package cn.ac.lz233.tarnhelm.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import rikka.material.app.DayNightDelegate

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //adapt status bar
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (mode == Configuration.UI_MODE_NIGHT_NO) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DayNightDelegate.setDefaultNightMode(DayNightDelegate.MODE_NIGHT_YES)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
}