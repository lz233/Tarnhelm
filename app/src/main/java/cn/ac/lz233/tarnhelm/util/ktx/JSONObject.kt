package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RedirectRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import org.json.JSONObject

fun JSONObject.insertToParameterRules(type: Int = 1, enabled: Boolean = true): ParameterRule {
    val item = ParameterRule(
        App.parameterRuleDao.getMaxId() + 1,
        this.getString("a"),
        this.getString("e"),
        this.getInt("f"),
        this.getJSONArray("g").toString(),
        this.getString("d"),
        type,
        enabled
    )
    App.parameterRuleDao.insert(item)
    return item
}

fun JSONObject.insertToRegexRules(type: Int = 1, enabled: Boolean = true): RegexRule {
    val item = RegexRule(
        App.regexRuleDao.getMaxId() + 1,
        this.getString("a"),
        this.getJSONArray("b").toString(),
        this.getJSONArray("c").toString(),
        this.getString("d"),
        type,
        enabled
    )
    App.regexRuleDao.insert(item)
    return item
}

fun JSONObject.insertToRedirectRules(type: Int = 1, enabled: Boolean = true): RedirectRule {
    val item = RedirectRule(
        App.redirectRuleDao.getMaxId() + 1,
        this.getString("a"),
        this.getString("e"),
        this.getString("h"),
        this.getString("d"),
        type,
        enabled
    )
    App.redirectRuleDao.insert(item)
    return item
}