package cn.ac.lz233.tarnhelm.extension

import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt.ExtInfo

data class ExtensionRecord(
    var enabled: Boolean = false,
    val entryClassName: String = ENTRY_CLASS_NAME,
    val id: String,
    val author: String?,
    val name: String,
    val description: String?,
    val extensionURL: String?,
    val versionCode: Int,
    val versionName: String?,
    val hasConfigurationPanel: Boolean,
    val minTarnhelmSdkVersion: Int,
    val minAndroidSdkVersion: Int,
    val regexes: List<String>
) {

    fun toExtInfo(): ExtInfo {
        return object : ExtInfo {
            override fun id(): String = id
            override fun author(): String? = author
            override fun name(): String = name
            override fun description(): String? = description
            override fun extensionURL(): String? = extensionURL
            override fun versionCode(): Int = versionCode
            override fun versionName(): String? = versionName
            override fun hasConfigurationPanel(): Boolean = hasConfigurationPanel
            override fun minTarnhelmSdkVersion(): Int = minTarnhelmSdkVersion
            override fun minAndroidSdkVersion(): Int = minAndroidSdkVersion
            override fun regexes() = regexes.toTypedArray<String>()
        }
    }

    companion object {
        const val ENTRY_CLASS_NAME = "TarnhelmExt"

        fun fromExtInfo(extInfo: ExtInfo): ExtensionRecord {
            return ExtensionRecord(
                id = extInfo.id(),
                author = extInfo.author(),
                name = extInfo.name(),
                description = extInfo.description(),
                extensionURL = extInfo.extensionURL(),
                versionCode = extInfo.versionCode(),
                versionName = extInfo.versionName(),
                hasConfigurationPanel = extInfo.hasConfigurationPanel(),
                minTarnhelmSdkVersion = extInfo.minTarnhelmSdkVersion(),
                minAndroidSdkVersion = extInfo.minAndroidSdkVersion(),
                regexes = extInfo.regexes().toList()
            )
        }

    }

}
