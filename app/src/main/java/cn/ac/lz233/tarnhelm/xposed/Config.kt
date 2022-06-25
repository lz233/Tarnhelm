package cn.ac.lz233.tarnhelm.xposed

import cn.ac.lz233.tarnhelm.BuildConfig

object Config {
    const val packageName = BuildConfig.APPLICATION_ID
    const val bridgeAction = "cn.ac.lz233.tarnhelm.bridge"
    lateinit var classLoader: ClassLoader
}