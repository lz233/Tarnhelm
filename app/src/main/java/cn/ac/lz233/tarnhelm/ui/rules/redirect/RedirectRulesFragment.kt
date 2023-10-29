package cn.ac.lz233.tarnhelm.ui.rules.redirect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.FragmentRedirectRulesBinding
import cn.ac.lz233.tarnhelm.ui.rules.DragSwipeCallback

class RedirectRulesFragment : Fragment() {

    private val binding by lazy { FragmentRedirectRulesBinding.inflate(layoutInflater) }
    val rulesList by lazy { App.redirectRuleDao.getAll() }
    val adapter by lazy { RedirectRulesAdapter(rulesList) }
    private val touchHelper by lazy { ItemTouchHelper(DragSwipeCallback(adapter)) }
    private var previousRulesListCount = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rulesRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.rulesRecyclerView.adapter = adapter
        touchHelper.attachToRecyclerView(binding.rulesRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        refreshRulesList()
    }

    private fun refreshRulesList() {
        if (previousRulesListCount == -1) {
            previousRulesListCount = rulesList.size
        } else {
            previousRulesListCount = rulesList.size
            rulesList.clear()
            rulesList.addAll(App.redirectRuleDao.getAll())
            adapter.notifyItemInserted(previousRulesListCount)
        }
    }
}