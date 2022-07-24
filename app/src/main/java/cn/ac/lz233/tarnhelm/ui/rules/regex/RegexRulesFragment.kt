package cn.ac.lz233.tarnhelm.ui.rules.regex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.FragmentRegexRulesBinding
import cn.ac.lz233.tarnhelm.ui.rules.DragSwipeCallback

class RegexRulesFragment : Fragment() {

    private val binding by lazy { FragmentRegexRulesBinding.inflate(layoutInflater) }
    val rulesList by lazy { App.regexRuleDao.getAll() }
    val adapter by lazy { RegexRulesAdapter(rulesList) }
    private val touchHelper by lazy { ItemTouchHelper(DragSwipeCallback(adapter)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rulesRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.rulesRecyclerView.adapter = adapter
        touchHelper.attachToRecyclerView(binding.rulesRecyclerView)
    }

    fun refreshRulesList() {
        rulesList.clear()
        rulesList.addAll(App.regexRuleDao.getAll())
    }
}