package cn.ac.lz233.tarnhelm.ui.rules

import android.content.ClipData
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.DialogRegexRuleEditBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.encodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.toJSONArray
import cn.ac.lz233.tarnhelm.util.ktx.toJSONObject
import cn.ac.lz233.tarnhelm.util.ktx.toMultiString
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import org.json.JSONArray

class RegexRulesAdapter(private val rulesList: MutableList<Rule>) : RecyclerView.Adapter<RegexRulesAdapter.ViewHolder>(), IDragSwipe {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleContentCardView: MaterialCardView = view.findViewById(R.id.ruleContentCardView)
        val ruleEnableSwitch: MaterialSwitch = view.findViewById(R.id.ruleEnableSwitch)
        val descriptionContentTextView: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val regexContentTextView: AppCompatTextView = view.findViewById(R.id.regexContentTextView)
        val replacementContentTextView: AppCompatTextView = view.findViewById(R.id.replacementContentTextView)
        val authorContentTextView: AppCompatTextView = view.findViewById(R.id.authorContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_regex_rule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rulesList[position]
        holder.ruleContentCardView.setOnClickListener {
            val dialogBinding = DialogRegexRuleEditBinding.inflate(LayoutInflater.from(holder.itemView.context))
            dialogBinding.descriptionEditText.setText(rule.description)
            dialogBinding.regexEditText.setText(JSONArray(rule.regexArray).toMultiString())
            dialogBinding.replacementEditText.setText(JSONArray(rule.replaceArray).toMultiString())
            dialogBinding.authorEditText.setText(rule.author)
            if (rule.sourceType != 0) dialogBinding.authorEditText.isEnabled = false
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.regexRulesDialogPositiveButton) { _, _ ->
                    val item = Rule(
                        rule.id,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.regexEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.replacementEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.authorEditText.text.toString(),
                        0,
                        true
                    )
                    App.ruleDao.insert(item)
                    rulesList[position] = item
                    notifyItemChanged(position)
                }
                .show()
            dialogBinding.copyImageView.setOnClickListener {
                App.clipboard.setPrimaryClip(ClipData.newPlainText("Tarnhelm", rule.toJSONObject().toString().encodeBase64()))
                dialog.dismiss()
            }
            dialogBinding.shareImageView.setOnClickListener {
                App.context.startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, rule.toJSONObject().toString().encodeBase64())
                    type = "text/plain"
                }, null).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                dialog.dismiss()
            }
            dialogBinding.deleteImageView.setOnClickListener {
                App.ruleDao.delete(rule)
                rulesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position - 1, itemCount - position + 1)
                dialog.dismiss()
            }
        }
        holder.ruleEnableSwitch.isChecked = rule.enabled
        holder.ruleEnableSwitch.setOnCheckedChangeListener { compoundButton, b ->
            val item = Rule(
                rule.id,
                rule.description,
                rule.regexArray,
                rule.replaceArray,
                rule.author,
                rule.sourceType,
                b
            )
            App.ruleDao.insert(item)
            rulesList[position] = item
        }
        holder.descriptionContentTextView.text = rule.description
        holder.regexContentTextView.text = JSONArray(rule.regexArray).toMultiString()
        holder.replacementContentTextView.text = JSONArray(rule.replaceArray).toMultiString()
        holder.authorContentTextView.text = rule.author
    }

    override fun getItemCount() = rulesList.size

    override fun onItemSwapped(fromPosition: Int, toPosition: Int) {
        LogUtil.d("onItemSwapped $fromPosition $toPosition")
        val fromRule = rulesList[fromPosition]
        val toRule = rulesList[toPosition]
        val newFromRule = Rule(
            toRule.id,
            fromRule.description,
            fromRule.regexArray,
            fromRule.replaceArray,
            fromRule.author,
            fromRule.sourceType,
            fromRule.enabled
        )
        val newToRule = Rule(
            fromRule.id,
            toRule.description,
            toRule.regexArray,
            toRule.replaceArray,
            toRule.author,
            toRule.sourceType,
            toRule.enabled
        )
        App.ruleDao.insert(newFromRule)
        App.ruleDao.insert(newToRule)
        rulesList[fromPosition] = newToRule
        rulesList[toPosition] = newFromRule
        notifyItemMoved(fromPosition, toPosition)
        //notifyItemChanged(fromPosition)
        //notifyItemChanged(toPosition)
    }

    override fun onItemDeleted(position: Int) {
        LogUtil.d("onItemDeleted")
        /*App.ruleDao.delete(rulesList[position])
        rulesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position - 1, itemCount - position + 1)*/
    }

    override fun onItemCopy(position: Int) {
        LogUtil.d("onItemCopy")
        //TODO
    }

}