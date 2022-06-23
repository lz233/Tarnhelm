package cn.ac.lz233.tarnhelm.ui.rules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.util.ktx.toMultiString
import org.json.JSONArray

class RulesAdapter(private val rulesList: List<Rule>) : RecyclerView.Adapter<RulesAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descriptionContentTextView: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val regexContentTextView: AppCompatTextView = view.findViewById(R.id.regexContentTextView)
        val replacementContentTextView: AppCompatTextView = view.findViewById(R.id.replacementContentTextView)
        val authorContentTextView: AppCompatTextView = view.findViewById(R.id.authorContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rulesList[position]
        holder.descriptionContentTextView.text = rule.description
        holder.regexContentTextView.text = JSONArray(rule.regexArray).toMultiString()
        holder.replacementContentTextView.text = JSONArray(rule.replaceArray).toMultiString()
        holder.authorContentTextView.text = rule.author
    }

    override fun getItemCount() = rulesList.size

}