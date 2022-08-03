package cn.ac.lz233.tarnhelm.ui.process

import android.os.Bundle
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.decodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.insertToParameterRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRegexRules
import org.json.JSONObject

class ProcessRulesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data
        val parameterRuleString = data?.getQueryParameter("parameter")
        val regexRuleString = data?.getQueryParameter("regex")
        when {
            parameterRuleString != null -> {
                val item = JSONObject(parameterRuleString.decodeBase64()).insertToParameterRules()
                LogUtil.toast(getString(R.string.rule_added_toast, item.description))
            }
            regexRuleString != null -> {
                val item = JSONObject(regexRuleString.decodeBase64()).insertToRegexRules()
                LogUtil.toast(getString(R.string.rule_added_toast, item.description))
            }
        }
        finish()
    }
}