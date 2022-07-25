package cn.ac.lz233.tarnhelm.util.ktx

import org.json.JSONArray

fun JSONArray.toMultiString() = StringBuilder().apply {
    for (i in 0 until this@toMultiString.length()) {
        append(this@toMultiString[i])
        append('\n')
    }
    deleteCharAt(lastIndex)
}.toString()

fun <T> JSONArray.contain(element: T): Boolean {
    var result = false
    for (index in 0 until this.length()) {
        if (this[index] == element) {
            result = true
            break
        }
    }
    return result
}