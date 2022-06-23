package cn.ac.lz233.tarnhelm.util.ktx

fun List<String>.toMultiString() = StringBuilder().apply {
    this@toMultiString.forEach {
        append(it)
        append('\n')
    }
    deleteCharAt(lastIndex)
}.toString()