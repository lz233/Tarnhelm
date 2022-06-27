package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.App

fun Int.getString() = App.context.getString(this)