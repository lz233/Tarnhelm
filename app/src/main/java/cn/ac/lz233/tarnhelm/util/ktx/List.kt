package cn.ac.lz233.tarnhelm.util.ktx

fun List<String>.toString(insert: String) = StringBuilder().apply {
    this@toString.forEach {
        append(it)
        append(insert)
    }
    delete(lastIndex - insert.length + 1, lastIndex + 1)
}.toString()