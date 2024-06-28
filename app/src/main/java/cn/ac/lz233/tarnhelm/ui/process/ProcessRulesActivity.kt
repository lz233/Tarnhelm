package cn.ac.lz233.tarnhelm.ui.process

import android.os.Bundle
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.ui.BaseActivity
import cn.ac.lz233.tarnhelm.util.LogUtil
import cn.ac.lz233.tarnhelm.util.ktx.decodeBase64
import cn.ac.lz233.tarnhelm.util.ktx.decodeURL
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.insertToParameterRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRedirectRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRegexRules
import org.json.JSONObject

class ProcessRulesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data
        val parameterRuleString = data?.getQueryParameter("parameter")
        val regexRuleString = data?.getQueryParameter("regex")
        val redirectRuleString = data?.getQueryParameter("redirect")
        LogUtil._d("parameterRuleString $parameterRuleString")
        LogUtil._d("regexRuleString $regexRuleString")
        LogUtil._d("redirectRuleString $redirectRuleString")
        try {
            when {
                parameterRuleString != null -> {
                    val item = JSONObject(parameterRuleString.decodeURL().decodeBase64()).insertToParameterRules()
                    LogUtil.toast(getString(R.string.rule_added_toast, item.description))
                }

                regexRuleString != null -> {
                    val item = JSONObject(regexRuleString.decodeURL().decodeBase64()).insertToRegexRules()
                    LogUtil.toast(getString(R.string.rule_added_toast, item.description))
                }

                redirectRuleString != null -> {
                    val item = JSONObject(redirectRuleString.decodeURL().decodeBase64()).insertToRedirectRules()
                    LogUtil.toast(getString(R.string.rule_added_toast, item.description))
                }
            }
        } catch (e: Throwable) {
            LogUtil.toast(R.string.rule_added_failed_toast.getString())
            LogUtil.e(e)
        }
        finish()
    }
}