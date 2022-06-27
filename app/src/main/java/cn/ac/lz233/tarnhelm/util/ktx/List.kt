package cn.ac.lz233.tarnhelm.util.ktx

fun List<String>.toString(insert: String) = StringBuilder().apply {
    this@toString.forEach {
        append(it)
        append(insert)
    }
    delete(lastIndex - insert.length + 1, lastIndex + 1)
}.toString()

fun List<String>.toString(insert: String, insertLast: String) = StringBuilder().apply {
    for ((index, string) in this@toString.withIndex()) {
        append(string)
        if (index == this@toString.lastIndex - 1)
            append(insertLast)
        else if (index != this@toString.lastIndex)
            append(insert)
    }
}.toString()

fun List<String>.toString(insertList: List<String>) = StringBuilder().apply {
    var insertListIndex = 0
    this@toString.forEach {
        append(it)
        append(insertList.getOrNull(insertListIndex++))
    }
}.toString()
