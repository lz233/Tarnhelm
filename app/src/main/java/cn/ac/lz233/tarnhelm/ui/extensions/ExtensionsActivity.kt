package cn.ac.lz233.tarnhelm.ui.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import cn.ac.lz233.tarnhelm.databinding.ActivityExtensionsBinding
import cn.ac.lz233.tarnhelm.ui.SecondaryBaseActivity

class ExtensionsActivity : SecondaryBaseActivity() {

    private val binding by lazy { ActivityExtensionsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(toolbar)



        binding.openWebImageView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://tarnhelm.project.ac.cn/rules.html")))
        }

        binding.importFab.setOnClickListener {

        }
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, ExtensionsActivity::class.java))
    }
}