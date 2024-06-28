package cn.ac.lz233.tarnhelm.util.ktx

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.logic.dao.SettingsDao
import cn.ac.lz233.tarnhelm.util.LogUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import kotlin.concurrent.thread

fun String.openUrl() = App.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse(this@openUrl)
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
})

fun CharSequence.toHtml(flags: Int = 0) = HtmlCompat.fromHtml(this.toString(), flags)

fun String.encodeBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("\n", "")

fun String.decodeBase64() = String(Base64.decode(this, Base64.DEFAULT))

fun String.encodeURL(): String = Uri.encode(this)

// DO NOT use java.net.URLDecoder since do not follow RFC3986
fun String.decodeURL(): String = Uri.decode(this)

fun String.toJSONArray() = JSONArray().apply {
    val stringList = split('\n')
    stringList.forEach {
        put(it)
    }
}

fun String.doTarnhelm(): Triple<CharSequence, Boolean, List<String>> {
    LogUtil._d("Original URL: $this")
    var result = this
    var hasTimeConsumingOperation = false
    val targetRules = mutableListOf<String>()
    var httpUrl = result.toHttpUrlOrNull()
    LogUtil._d("HTTP URL: $httpUrl")
    if (httpUrl != null) {
        val redirectRules = App.redirectRuleDao.getAll()
        for (rule in redirectRules) {
            if ((rule.enabled) and (rule.domain == httpUrl!!.host)) {
                LogUtil._d("Target Redirect Rule: (${rule.id}) ${rule.description}")
                hasTimeConsumingOperation = true
                targetRules.add("[${R.string.rulesRedirectsTitle.getString()}]${rule.description.replace("\n", "↵")}")
                val notification = NotificationCompat.Builder(App.context, "234")
                    .setContentTitle(R.string.process_result_processing.getString())
                    //.setContentText(methodResult)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setTimeoutAfter(10000)
                    .setProgress(100, 0, true)
                    .build()
                App.notificationManager.notify(234, notification)
                httpUrl = httpUrl.followRedirect(rule.userAgent)
            }
        }
        result = httpUrl.toString()
        LogUtil._d("After Redirects: $result")
        val parameterRules = App.parameterRuleDao.getAll()
        for (rule in parameterRules) {
            if ((rule.enabled) and (rule.domain == httpUrl!!.host)) {
                LogUtil._d("Target Parameter Rule: (${rule.id}) ${rule.description}")
                targetRules.add("[${R.string.rulesParametersTitle.getString()}]${rule.description.replace("\n", "↵")}")
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
        result = httpUrl.toString().decodeURL()
        LogUtil._d("After Parameters: $result")
    }
    val regexRules = App.regexRuleDao.getAll()
    for ((index, rule) in regexRules.withIndex()) {
        if (rule.enabled) {
            val regexArray = JSONArray(rule.regexArray)
            val replaceArray = JSONArray(rule.replaceArray)
            if (regexArray.length() != replaceArray.length()) throw Throwable("non-standard array")
            matchRule@ for (i in 0 until regexArray.length()) {
                val regex = Regex(regexArray.getString(i))
                if (i != 0 || regex.containsMatchIn(result)) {
                    if (i == 0) {
                        LogUtil._d("Target Regex Rule: ${rule.id}@${rule.description}")
                        targetRules.add("[${R.string.rulesRegexesTitle.getString()}]${rule.description.replace("\n", "↵")}")
                    }
                    result = regex.replace(result, replaceArray.getString(i))
                    LogUtil._d("[${rule.description}]($i): $result")
                } else {
                    break@matchRule
                }
            }
        }
    }
    if (targetRules.isEmpty()) result = this
    LogUtil._d("Result: $result")
    LogUtil._d("TargetRules: $targetRules")
    return Triple(result, hasTimeConsumingOperation, targetRules)
}

@SuppressLint("RestrictedApi")
fun CharSequence.doTarnhelms(): Triple<CharSequence, Boolean, List<List<String>>> {
    var methodResult = this
    var hasTimeConsumingOperation = false
    val targetRules = mutableListOf<List<String>>()
    methodResult =
            //Regex("""((https|http)://)(\p{L}|\p{Nd})+\.\p{L}+(:\p{Nd})?(\p{Ll}|\p{Lu}|\p{Nd}|/|\?|\+|&|=|\.|-|_|#|%)*""")
        Regex("""(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s\uFF01-\uFF5E\u4e00-\u9fff\u3400-\u4DBF\u3040-\u309F\u30A0-\u30FF\uAC00-\uD7AF\u3000-\u303F]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s\uFF01-\uFF5E\u4e00-\u9fff\u3400-\u4DBF\u3040-\u309F\u30A0-\u30FF\uAC00-\uD7AF\u3000-\u303F]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s\uFF01-\uFF5E\u4e00-\u9fff\u3400-\u4DBF\u3040-\u309F\u30A0-\u30FF\uAC00-\uD7AF\u3000-\u303F]{2,}|www\.[a-zA-Z0-9]+\.[^\s\uFF01-\uFF5E\u4e00-\u9fff\u3400-\u4DBF\u3040-\u309F\u30A0-\u30FF\uAC00-\uD7AF\u3000-\u303F]{2,})""")
            // PatternsCompat.AUTOLINK_WEB_URL.toRegex() cannot recognize some irregular sharing texts
            // 73 xx发布了一篇小红书笔记，快来看吧！ 😆 xxxxxxxxxxxxx 😆 http://xhslink.com/xxxxxx，复制本条信息，打开【小红书】App查看精彩内容！
            .replace(this) {
                val result = it.value.doTarnhelm()
                if (result.second) hasTimeConsumingOperation = true
                targetRules.add(result.third)
                result.first
            }
    return Triple(methodResult, hasTimeConsumingOperation, targetRules)
}

fun CharSequence.doTarnhelms(join: Boolean = false, callback: (success: Boolean, result: String) -> Unit = { _, _ -> }): Pair<Boolean, String> {
    var methodResult = this;
    var hasTimeConsumingOperation = false
    var targetRules = emptyList<List<String>>()
    var success = false;
    val thread = thread {
        runCatching {
            val result = this.doTarnhelms()
            methodResult = result.first
            hasTimeConsumingOperation = result.second
            targetRules = result.third
        }.onSuccess {
            success = true
            callback(true, methodResult.toString())
            if (methodResult != this) {
                if (SettingsDao.alwaysSendProcessingNotification or hasTimeConsumingOperation) {
                    val notification = NotificationCompat.Builder(App.context, "234")
                        .setContentTitle(R.string.process_result_success.getString())
                        .setContentText(targetRules.toFlowString())
                        .setSmallIcon(R.drawable.ic_icon)
                        .setShowWhen(false)
                        .setTimeoutAfter(500)
                        .build()
                    App.notificationManager.notify(234, notification)
                }
            }
        }.onFailure { throwable ->
            success = false
            callback(false, methodResult.toString())
            LogUtil.e(throwable)
            val notification = NotificationCompat.Builder(App.context, "234")
                .setContentTitle(R.string.process_result_failed.getString())
                .setContentText(throwable.localizedMessage)
                .setSmallIcon(R.drawable.ic_icon)
                .setShowWhen(false)
                .setTimeoutAfter(1000)
                .build()
            App.notificationManager.notify(234, notification)
        }
    }
    if (join) thread.join()
    return success to methodResult.toString()
}