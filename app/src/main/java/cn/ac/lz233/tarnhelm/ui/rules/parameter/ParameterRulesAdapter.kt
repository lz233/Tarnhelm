package cn.ac.lz233.tarnhelm.ui.rules.parameter

import android.content.ClipData
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.DialogParameterRuleEditBinding
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.ui.rules.IDragSwipe
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.*
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import org.json.JSONArray

class ParameterRulesAdapter(private val rulesList: MutableList<ParameterRule>) : RecyclerView.Adapter<ParameterRulesAdapter.ViewHolder>(), IDragSwipe {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleContentCardView: MaterialCardView = view.findViewById(R.id.ruleContentCardView)
        val ruleEnableSwitch: MaterialSwitch = view.findViewById(R.id.ruleEnableSwitch)
        val descriptionContentTextView: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val domainContentTextView: AppCompatTextView = view.findViewById(R.id.domainContentTextView)
        val modeContentTextView: AppCompatTextView = view.findViewById(R.id.modeContentTextView)
        val parametersContentTextView: AppCompatTextView = view.findViewById(R.id.parametersContentTextView)
        val authorContentTextView: AppCompatTextView = view.findViewById(R.id.authorContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parameter_rule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rulesList[position]
        holder.ruleContentCardView.setOnClickListener {
            val dialogBinding = DialogParameterRuleEditBinding.inflate(LayoutInflater.from(holder.itemView.context))
            val base64Text = (if (SettingsDao.exportRulesAsLink) "tarnhelm://rule?parameter=" else "") + rule.toJSONObject().toString().encodeBase64()
            dialogBinding.modeToggleButton.check(rule.mode.getModeButtonId())
            dialogBinding.descriptionEditText.setText(rule.description)
            dialogBinding.domainEditText.setText(rule.domain)
            dialogBinding.parametersEditText.setText(JSONArray(rule.parametersArray).toMultiString())
            dialogBinding.authorEditText.setText(rule.author)
            if (rule.sourceType != 0) dialogBinding.authorEditText.isEnabled = false
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.parameterRulesDialogPositiveButton) { _, _ ->
                    val item = ParameterRule(
                        rule.id,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.domainEditText.text.toString(),
                        dialogBinding.modeToggleButton.checkedButtonId.getModeId(),
                        dialogBinding.parametersEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.authorEditText.text.toString(),
                        0,
                        true
                    )
                    App.parameterRuleDao.insert(item)
                    rulesList[position] = item
                    notifyItemChanged(position)
                }
                .show()
            dialogBinding.copyImageView.setOnClickListener {
                App.clipboard.setPrimaryClip(ClipData.newPlainText("Tarnhelm", base64Text))
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
                App.parameterRuleDao.delete(rule)
                rulesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position - 1, itemCount - position + 1)
                dialog.dismiss()
            }
        }
        holder.ruleEnableSwitch.isChecked = rule.enabled
        holder.ruleEnableSwitch.setOnCheckedChangeListener { compoundButton, b ->
            val item = ParameterRule(
                rule.id,
                rule.description,
                rule.domain,
                rule.mode,
                rule.parametersArray,
                rule.author,
                rule.sourceType,
                b
            )
            App.parameterRuleDao.insert(item)
            rulesList[position] = item
        }
        holder.descriptionContentTextView.text = rule.description
        holder.domainContentTextView.text = rule.domain
        holder.modeContentTextView.text = when (rule.mode) {
            0 -> R.string.parameterRulesItemWhiteListMode.getString()
            1 -> R.string.parameterRulesItemBlackListMode.getString()
            else -> ""
        }
        holder.parametersContentTextView.text = JSONArray(rule.parametersArray).toMultiString()
        holder.authorContentTextView.text = rule.author
    }

    override fun getItemCount() = rulesList.size

    override fun onItemSwapped(fromPosition: Int, toPosition: Int) {
        LogUtil._d("onItemSwapped $fromPosition $toPosition")
        val fromRule = rulesList[fromPosition]
        val toRule = rulesList[toPosition]
        val newFromRule = ParameterRule(
            toRule.id,
            fromRule.description,
            fromRule.domain,
            fromRule.mode,
            fromRule.parametersArray,
            fromRule.author,
            fromRule.sourceType,
            fromRule.enabled
        )
        val newToRule = ParameterRule(
            fromRule.id,
            toRule.description,
            toRule.domain,
            toRule.mode,
            toRule.parametersArray,
            toRule.author,
            toRule.sourceType,
            toRule.enabled
        )
        App.parameterRuleDao.insert(newFromRule)
        App.parameterRuleDao.insert(newToRule)
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
    }

}