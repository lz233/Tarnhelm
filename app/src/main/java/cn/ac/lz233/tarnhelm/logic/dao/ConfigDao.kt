package cn.ac.lz233.tarnhelm.logic.dao

import cn.ac.lz233.tarnhelm.App

object ConfigDao {
    var ranked: Boolean
        get() = App.sp.getBoolean("ranked", false)
        set(value) = App.editor.putBoolean("ranked", value).apply()

    var openTimes: Int
        get() = App.sp.getInt("openTimes", 0)
        set(value) = App.editor.putInt("openTimes", if (value >= Int.MAX_VALUE) 0 else value).apply()
}