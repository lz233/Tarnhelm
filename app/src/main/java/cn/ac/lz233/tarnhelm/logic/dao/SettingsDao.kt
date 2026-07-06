package cn.ac.lz233.tarnhelm.logic.dao

import cn.ac.lz233.tarnhelm.App

object SettingsDao {
    val workModeEditTextMenu
        get() = App.spSettings.getBoolean("workModeEditTextMenu", true)

    val workModeCopyMenu
        get() = App.spSettings.getBoolean("workModeCopyMenu", true)

    val workModeShare
        get() = App.spSettings.getBoolean("workModeShare", true)

    val workModeBackgroundMonitoring
        get() = App.spSettings.getBoolean("workModeBackgroundMonitoring", false)

    val alwaysSendProcessingNotification
        get() = App.spSettings.getBoolean("alwaysSendProcessingNotification", false)

    val exportRulesAsLink
        get() = App.spSettings.getBoolean("exportRulesAsLink", false)

    val enableDefaultUA
        get() = App.spSettings.getBoolean("enableDefaultUA", true)

    val defaultUA
        get() = App.webSettings.userAgentString
    val overrideUA
        get() = App.spSettings.getString("overrideUA", "")
}
