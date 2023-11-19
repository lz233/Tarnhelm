package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.logic.Network
import cn.ac.lz233.tarnhelm.util.LogUtil
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request

fun HttpUrl.followRedirectOnce() = runCatching {
    val response = Network.okHttpClientNoRedirect
        .newCall(Request.Builder().url(this).build())
        .execute()
    if (response.isRedirect) {
        response.header("Location")?.toHttpUrlOrNull() ?: response.header("location")?.toHttpUrlOrNull() ?: this
    } else {
        this
    }
}.onFailure {
    LogUtil.e(it)
}.getOrThrow()