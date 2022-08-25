package cn.ac.lz233.tarnhelm.ui.rules.regex

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
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import cn.ac.lz233.tarnhelm.ui.rules.IDragSwipe
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.*
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import org.json.JSONArray

class RegexRulesAdapter(private val rulesList: MutableList<RegexRule>) : RecyclerView.Adapter<RegexRulesAdapter.ViewHolder>(), IDragSwipe {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleContentCardView: MaterialCardView = view.findViewById(R.id.ruleContentCardView)
        val ruleEnableSwitch: MaterialSwitch = view.findViewById(R.id.ruleEnableSwitch)
        val descriptionContentTextView: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val regexesContentTextView: AppCompatTextView = view.findViewById(R.id.regexesContentTextView)
        val replacementsContentTextView: AppCompatTextView = view.findViewById(R.id.replacementsContentTextView)
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
            val base64Text = (if (SettingsDao.exportRulesAsLink) "tarnhelm://rule?regex=" else "") + rule.toJSONObject().toString().encodeBase64().encodeURL()
            dialogBinding.descriptionEditText.setText(rule.description)
            dialogBinding.regexesEditText.setText(JSONArray(rule.regexArray).toMultiString())
            dialogBinding.replacementsEditText.setText(JSONArray(rule.replaceArray).toMultiString())
            dialogBinding.authorEditText.setText(rule.author)
            if (rule.sourceType != 0) dialogBinding.authorEditText.isEnabled = false
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.regexRulesDialogPositiveButton) { _, _ ->
                    val item = RegexRule(
                        rule.id,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.regexesEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.replacementsEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.authorEditText.text.toString(),
                        rule.sourceType,
                        rule.enabled
                    )
                    App.regexRuleDao.insert(item)
                    rulesList[position] = item
                    notifyItemChanged(position)
                }
                .show()
            dialogBinding.copyImageView.setOnClickListener {
                App.clipboardManager.setPrimaryClip(ClipData.newPlainText("Tarnhelm", base64Text))
                dialog.dismiss()
            }
            dialogBinding.shareImageView.setOnClickListener {
                App.context.startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, base64Text)
                    type = "text/plain"
                }, null).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                dialog.dismiss()
            }
            dialogBinding.deleteImageView.setOnClickListener {
                App.regexRuleDao.delete(rule)
                rulesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position - 1, itemCount - position + 1)
                dialog.dismiss()
            }
        }
        holder.ruleEnableSwitch.isChecked = rule.enabled
        holder.ruleEnableSwitch.setOnCheckedChangeListener { compoundButton, b ->
            val item = RegexRule(
                rule.id,
                rule.description,
                rule.regexArray,
                rule.replaceArray,
                rule.author,
                rule.sourceType,
                b
            )
            App.regexRuleDao.insert(item)
            rulesList[position] = item
        }
        holder.descriptionContentTextView.text = rule.description
        holder.regexesContentTextView.text = JSONArray(rule.regexArray).toMultiString()
        holder.replacementsContentTextView.text = JSONArray(rule.replaceArray).toMultiString()
        holder.authorContentTextView.text = rule.author
    }

    override fun getItemCount() = rulesList.size

    override fun onItemSwapped(fromPosition: Int, toPosition: Int) {
        LogUtil._d("onItemSwapped $fromPosition $toPosition")
        val fromRule = rulesList[fromPosition]
        val toRule = rulesList[toPosition]
        val newFromRule = RegexRule(
            toRule.id,
            fromRule.description,
            fromRule.regexArray,
            fromRule.replaceArray,
            fromRule.author,
            fromRule.sourceType,
            fromRule.enabled
        )
        val newToRule = RegexRule(
            fromRule.id,
            toRule.description,
            toRule.regexArray,
            toRule.replaceArray,
            toRule.author,
            toRule.sourceType,
            toRule.enabled
        )
        App.regexRuleDao.insert(newFromRule)
        App.regexRuleDao.insert(newToRule)
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