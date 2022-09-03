package cn.ac.lz233.tarnhelm.util

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

object AppCenterUtil {
    fun initAppCenter(application: Application) = AppCenter.start(application, "d6f67bf8-858b-451a-98e9-c2c295474e9a", Analytics::class.java, Crashes::class.java)

    fun setAnalyticsEnabled(enabled: Boolean) = Analytics.setEnabled(enabled)

    fun setCrashesEnabled(enabled: Boolean) = Crashes.setEnabled(enabled)
}
