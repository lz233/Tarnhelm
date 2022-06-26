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
    rules.forEach {
        val regexArray = JSONArray(it.regexArray)
        val replaceArray = JSONArray(it.replaceArray)
        if (regexArray.length() != replaceArray.length()) throw Throwable("non-standard array")
        for (i in 0 until regexArray.length()) {
            //LogUtil.d(regexArray.getString(i))
            result = Regex(regexArray.getString(i)).replace(result, replaceArray.getString(i))
        }
    }
    return result
}