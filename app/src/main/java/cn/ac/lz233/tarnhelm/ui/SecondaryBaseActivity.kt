package cn.ac.lz233.tarnhelm.ui

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitController
import cn.ac.lz233.tarnhelm.R
import kotlinx.coroutines.launch

abstract class SecondaryBaseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                SplitController.getInstance(this@SecondaryBaseActivity).splitInfoList(this@SecondaryBaseActivity)
                    .collect { list ->
                        toolbar?.navigationIcon = if (list.isEmpty() || (list[0].splitAttributes.splitType == SplitAttributes.SplitType.SPLIT_TYPE_EXPAND)) {
                            toolbar?.setNavigationOnClickListener {
                                onBackPressedDispatcher.onBackPressed()
                            }
                            AppCompatResources.getDrawable(this@SecondaryBaseActivity, R.drawable.ic_arrow_back)
                        } else {
                            null
                        }
                    }
            }
        }
    }
}