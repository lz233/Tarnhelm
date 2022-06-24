package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.logic.module.meta.Rule
import org.json.JSONArray
import org.json.JSONObject

fun Rule.toJSONObject() = JSONObject().apply {
    put("a", this@toJSONObject.description)
    put("b", JSONArray(this@toJSONObject.regexArray))
    put("c", JSONArray(this@toJSONObject.replaceArray))
    put("d", this@toJSONObject.author)
}