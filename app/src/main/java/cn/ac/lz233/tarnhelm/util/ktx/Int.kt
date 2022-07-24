package cn.ac.lz233.tarnhelm.util.ktx

import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.R

fun Int.getString() = App.context.getString(this)

fun Int.getModeButtonId() = when (this) {
    0 -> R.id.whiteListModeButton
    1 -> R.id.blackListModeButton
    else -> R.id.whiteListModeButton
}

fun Int.getModeId() = when (this) {
    R.id.whiteListModeButton -> 0
    R.id.blackListModeButton -> 1
    else -> R.id.whiteListModeButton
}