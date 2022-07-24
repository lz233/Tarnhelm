package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule
import org.json.JSONArray
import org.json.JSONObject

fun RegexRule.toJSONObject() = JSONObject().apply {
    put("a", this@toJSONObject.description)
    put("b", JSONArray(this@toJSONObject.regexArray))
    put("c", JSONArray(this@toJSONObject.replaceArray))
    put("d", this@toJSONObject.author)
}

fun ParameterRule.toJSONObject() = JSONObject().apply {
    put("a", this@toJSONObject.description)
    put("e", this@toJSONObject.domain)
    put("f", this@toJSONObject.mode)
    put("g", JSONArray(this@toJSONObject.parametersArray))
    put("d", this@toJSONObject.author)
}