package cn.ac.lz233.tarnhelm.ui.rules.redirect

import android.content.ClipData
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.DialogRedirectRuleEditBinding
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.logic.module.meta.RedirectRule
import cn.ac.lz233.tarnhelm.ui.rules.IDragSwipe
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.encodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.encodeURL
import cn.ac.lz233.tarnhelm.util.ktx.toJSONObject
import cn.ac.lz233.tarnhelm.util.ktx.useFlymeChooser
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch

class RedirectRulesAdapter(private val rulesList: MutableList<RedirectRule>) : RecyclerView.Adapter<RedirectRulesAdapter.ViewHolder>(), IDragSwipe {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleContentCardView: MaterialCardView = view.findViewById(R.id.ruleContentCardView)
        val ruleEnableSwitch: MaterialSwitch = view.findViewById(R.id.ruleEnableSwitch)
        val descriptionContentTextView: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val domainContentTextView: AppCompatTextView = view.findViewById(R.id.domainContentTextView)
        val userAgentContentTextView: AppCompatTextView = view.findViewById(R.id.userAgentContentTextView)
        val authorContentTextView: AppCompatTextView = view.findViewById(R.id.authorContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_redirect_rule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rulesList[position]
        holder.ruleContentCardView.setOnClickListener {
            val dialogBinding = DialogRedirectRuleEditBinding.inflate(LayoutInflater.from(holder.itemView.context))
            val base64Text = (if (SettingsDao.exportRulesAsLink) "tarnhelm://rule?redirect=" else "") + rule.toJSONObject().toString().encodeBase64().encodeURL()
            dialogBinding.descriptionEditText.setText(rule.description)
            dialogBinding.domainEditText.setText(rule.domain)
            dialogBinding.userAgentEditText.setText(rule.userAgent)
            dialogBinding.authorEditText.setText(rule.author)
            dialogBinding.authorEditText.isEnabled = (rule.sourceType == 0) or BuildConfig.DEBUG
            val dialog = MaterialAlertDialogBuilder(holder.itemView.context)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.redirectRulesDialogPositiveButton) { _, _ ->
                    val item = RedirectRule(
                        rule.id,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.domainEditText.text.toString(),
                        dialogBinding.userAgentEditText.text.toString(),
                        dialogBinding.authorEditText.text.toString(),
                        rule.sourceType,
                        rule.enabled
                    )
                    App.redirectRuleDao.insert(item)
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
                }, null).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    useFlymeChooser(false)
                })
                dialog.dismiss()
            }
            dialogBinding.deleteImageView.setOnClickListener {
                App.redirectRuleDao.delete(rule)
                rulesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position - 1, itemCount - position + 1)
                dialog.dismiss()
            }
        }
        holder.ruleEnableSwitch.isChecked = rule.enabled
        holder.ruleEnableSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isShown) {
                val item = RedirectRule(
                    rule.id,
                    rule.description,
                    rule.domain,
                    rule.userAgent,
                    rule.author,
                    rule.sourceType,
                    b
                )
                App.redirectRuleDao.insert(item)
                rulesList[position] = item
            }
        }
        holder.descriptionContentTextView.text = rule.description
        holder.domainContentTextView.text = rule.domain
        holder.userAgentContentTextView.text = rule.userAgent
        holder.authorContentTextView.text = rule.author
    }

    override fun getItemCount() = rulesList.size

    override fun onItemSwapped(fromPosition: Int, toPosition: Int) {
        LogUtil._d("onItemSwapped $fromPosition $toPosition")
        val fromRule = rulesList[fromPosition]
        val toRule = rulesList[toPosition]
        val newFromRule = RedirectRule(
            toRule.id,
            fromRule.description,
            fromRule.domain,
            fromRule.userAgent,
            fromRule.author,
            fromRule.sourceType,
            fromRule.enabled
        )
        val newToRule = RedirectRule(
            fromRule.id,
            toRule.description,
            toRule.domain,
            toRule.userAgent,
            toRule.author,
            toRule.sourceType,
            toRule.enabled
        )
        App.redirectRuleDao.insert(newFromRule)
        App.redirectRuleDao.insert(newToRule)
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