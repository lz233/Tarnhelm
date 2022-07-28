package cn.ac.lz233.tarnhelm.xposed.module

import cn.ac.lz233.tarnhelm.xposed.util.setReturnConstant

object Self {

    fun init() {
        "cn.ac.lz233.tarnhelm.App\$Companion".setReturnConstant("isXposedActive", result = true)
    }

}