package cn.ac.lz233.tarnhelm.util.ktx

import android.content.Context
import cn.ac.lz233.tarnhelm.extension.ExtensionManager
import cn.ac.lz233.tarnhelm.extension.ExtensionRecord
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt.ExtInfo

fun ExtInfo.getExtPath(context: Context): String {
    return "${context.filesDir.absolutePath}/extensions/${ExtensionManager.toMD5(this.id())}/"
}

fun ExtensionRecord.getExtPath(context: Context): String {
    return "${context.filesDir.absolutePath}/extensions/${ExtensionManager.toMD5(this.id)}/"
}