package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.logic.Network
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import okhttp3.internal.commonToString
import org.json.JSONObject
import java.io.IOException

fun HttpUrl.followRedirect(userAgent: String?): HttpUrl {
    val response = Network.okHttpClientNoRedirect
        .newCall(
            Request.Builder()
                .url(this)
                .let {
                    if (userAgent.isNullOrEmpty()) it else it.addHeader("User-Agent", userAgent)
                }
                .build()
        )
        .execute()
    return if (response.isSuccessful) {
        // bilibili returns 404 but still using 200 header
        val body = response.body.string()
        runCatching {
            if (JSONObject(body).get("code").toString().contains("404")) throw IOException(body)
        }.onFailure {
            if (it is IOException) throw it
        }
        this
    } else if (response.isRedirect) {
        (response.header("Location")?.toHttpUrlOrNull() ?: response.header("location")?.toHttpUrlOrNull())
            ?.followRedirect(userAgent) ?: this
    } else {
        throw IOException(response.commonToString())
    }
}