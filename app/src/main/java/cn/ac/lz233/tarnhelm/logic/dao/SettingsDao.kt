package cn.ac.lz233.tarnhelm.logic.dao

import cn.ac.lz233.tarnhelm.App

object SettingsDao {
    val workOnEditTextMenu
        get() = App.spSettings.getBoolean("workOnEditTextMenu", true)
}