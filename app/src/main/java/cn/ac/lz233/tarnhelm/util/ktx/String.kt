package cn.ac.lz233.tarnhelm.util.ktx

import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.core.text.HtmlCompat
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.util.LogUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.openUrl() = App.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse(this@openUrl)
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
})

fun CharSequence.toHtml(flags: Int = 0) = HtmlCompat.fromHtml(this.toString(), flags)

fun String.encodeBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("\n", "")

fun String.decodeBase64() = String(Base64.decode(this, Base64.DEFAULT))

fun String.encodeURL(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.name())

fun String.decodeURL(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.name())

fun String.toJSONArray() = JSONArray().apply {
    val stringList = split('\n')
    stringList.forEach {
        put(it)
    }
}

fun String.doTarnhelm(): CharSequence {
    LogUtil._d("Original URL: $this")
    var result = this
    val parameterRules = App.parameterRuleDao.getAll()
    var httpUrl = result.toHttpUrlOrNull()
    LogUtil._d("HTTP URL: $httpUrl")
    if (httpUrl != null) {
        for (rule in parameterRules) {
            if ((rule.enabled) and (rule.domain == httpUrl!!.host)) {
                val ruleParameterNames = JSONArray(rule.parametersArray)
                val urlParameterNames = httpUrl.queryParameterNames
                val overlapParameterNames = mutableListOf<String>()
                val aloneParameterNames = mutableListOf<String>()
                urlParameterNames.forEach {
                    if (ruleParameterNames.contain(it)) overlapParameterNames.add(it) else aloneParameterNames.add(it)
                }
                httpUrl = httpUrl.run {
                    var httpUrlBuilder = this.newBuilder()
                    when (rule.mode) {
                        0 -> aloneParameterNames.forEach {
                            httpUrlBuilder = httpUrlBuilder.removeAllQueryParameters(it)
                        }

                        1 -> overlapParameterNames.forEach {
                            httpUrlBuilder = httpUrlBuilder.removeAllQueryParameters(it)
                        }
                    }
                    httpUrlBuilder.build()
                }
            }
        }
        result = httpUrl.toString()//.decodeURL()
        LogUtil._d("After Parameters: $result")
    }
    val regexRules = App.regexRuleDao.getAll()
    var skipRuleID = -1
    for ((index, rule) in regexRules.withIndex()) {
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
    LogUtil._d("Result: $result")
    return result
}

fun CharSequence.doTarnhelms(): String {
    var result = this.toString()
    result =
        Regex("""(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s\u2E80-\u9FFF]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s\u2E80-\u9FFF]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s\u2E80-\u9FFF]{2,}|www\.[a-zA-Z0-9]+\.[^\s\u2E80-\u9FFF]{2,})""")
            .replace(this) { it.value.doTarnhelm() }
    /*val notification = Notification.Builder(App.context, "234")
        .setContentTitle(R.string.process_result_success.getString())
        .setContentText(result)
        .setSmallIcon(R.drawable.ic_icon)
        .setShowWhen(false)
        .setTimeoutAfter(500)
        .build()
    App.notificationManager.notify(234, notification)*/
    return result
}
