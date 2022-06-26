package cn.ac.lz233.tarnhelm.ui.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.databinding.ActivityRulesBinding
import cn.ac.lz233.tarnhelm.databinding.DialogAddBinding
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.decodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.toJSONArray
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class RulesActivity : BaseActivity() {

    private val binding by lazy { ActivityRulesBinding.inflate(layoutInflater) }
    private val rulesList by lazy { App.ruleDao.getAll() }
    private val adapter by lazy { RulesAdapter(rulesList) }
    private val touchHelper by lazy { ItemTouchHelper(DragSwipeCallback(adapter)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.rulesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rulesRecyclerView.adapter = adapter
        touchHelper.attachToRecyclerView(binding.rulesRecyclerView)
        binding.addFab.setOnClickListener {
            val dialogBinding = DialogAddBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.root)
                .setPositiveButton("OK") { _, _ ->
                    val item = Rule(
                        App.ruleDao.getMaxId() + 1,
                        dialogBinding.descriptionEditText.text.toString(),
                        dialogBinding.regexEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.replacementEditText.text.toString().toJSONArray().toString(),
                        dialogBinding.authorEditText.text.toString(),
                        0,
                        true
                    )
                    App.ruleDao.insert(item)
                    rulesList.add(item)
                    adapter.notifyItemInserted(adapter.itemCount - 1)
                }
                .show()
            dialogBinding.pasteImageView.setOnClickListener {
                try {
                    val ruleJSONObject = JSONObject(App.clipboard.primaryClip!!.getItemAt(0).text.toString().decodeBase64())
                    val item = Rule(
                        App.ruleDao.getMaxId() + 1,
                        ruleJSONObject.getString("a"),
                        ruleJSONObject.getJSONArray("b").toString(),
                        ruleJSONObject.getJSONArray("c").toString(),
                        ruleJSONObject.getString("d"),
                        1,
                        true
                    )
                    App.ruleDao.insert(item)
                    rulesList.add(item)
                    adapter.notifyItemInserted(adapter.itemCount - 1)
                    dialog.dismiss()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun refreshRulesList() {
        rulesList.clear()
        rulesList.addAll(App.ruleDao.getAll())
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, RulesActivity::class.java))
    }
}