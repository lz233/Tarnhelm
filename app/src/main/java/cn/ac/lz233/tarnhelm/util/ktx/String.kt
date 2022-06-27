package cn.ac.lz233.tarnhelm.util.ktx

import android.util.Base64
import androidx.core.text.HtmlCompat
import cn.ac.lz233.tarnhelm.App
import org.json.JSONArray

fun CharSequence.toHtml(flags: Int = 0) = HtmlCompat.fromHtml(this.toString(), flags)

fun String.encodeBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)

fun String.decodeBase64() = String(Base64.decode(this, Base64.DEFAULT))

fun String.toJSONArray() = JSONArray().apply {
    val stringList = split('\n')
    stringList.forEach {
        put(it)
    }
}

fun String.doTarnhelm(): String {
    var result = this
    val rules = App.ruleDao.getAll()
    var skipRuleID = -1
    for ((index, rule) in rules.withIndex()) {
        if (rule.enabled) {
            val regexArray = JSONArray(rule.regexArray)
            val replaceArray = JSONArray(rule.replaceArray)
            if (regexArray.length() != replaceArray.length()) throw Throwable("non-standard array")
            matchRule@ for (i in 0 until regexArray.length()) {
                if (skipRuleID == index) break@matchRule
                val regex = Regex(regexArray.getString(i))
                if (regex.containsMatchIn(result) or (i != 0))
                    result = regex.replace(result, replaceArray.getString(i))
                else
                    skipRuleID = index
            }
        }
    }
    return result
}

fun String.doTarnhelms() =
    Regex("""(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s]{2,}|www\.[a-zA-Z0-9]+\.[^\s]{2,})""")
        .replace(this) { it.value.doTarnhelm() }