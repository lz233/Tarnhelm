package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.util.LogUtil
import org.json.JSONArray

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