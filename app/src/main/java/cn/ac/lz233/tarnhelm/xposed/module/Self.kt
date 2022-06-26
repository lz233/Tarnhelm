package cn.ac.lz233.tarnhelm.xposed.module

import cn.ac.lz233.tarnhelm.xposed.util.setReturnConstant

object Self {

    fun init() {
        "cn.ac.lz233.tarnhelm.App".setReturnConstant("isXposedActive", result = true)
    }

}