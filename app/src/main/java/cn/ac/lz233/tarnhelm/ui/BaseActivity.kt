package cn.ac.lz233.tarnhelm.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var rootView: View
    var toolbar: MaterialToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = findViewById(android.R.id.content)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
}