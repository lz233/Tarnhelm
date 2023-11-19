package cn.ac.lz233.tarnhelm.logic

import okhttp3.OkHttpClient

object Network {
    val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()
    val okHttpClientNoRedirect = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .followRedirects(false)
        .build()
}