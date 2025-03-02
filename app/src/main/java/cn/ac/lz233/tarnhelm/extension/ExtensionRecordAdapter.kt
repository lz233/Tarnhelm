package cn.ac.lz233.tarnhelm.extension

import io.packable.PackDecoder
import io.packable.PackEncoder
import io.packable.TypeAdapter

object ExtensionRecordAdapter : TypeAdapter<ExtensionRecord> {

    override fun decode(decoder: PackDecoder): ExtensionRecord {
        return ExtensionRecord(
            enabled = decoder.getBoolean(0),
            entryClassName = decoder.getString(1),
            id = decoder.getString(2),
            author = decoder.getString(3),
            name = decoder.getString(4),
            description = decoder.getString(5),
            extensionURL = decoder.getString(6),
            versionCode = decoder.getInt(7),
            versionName = decoder.getString(8),
            hasConfigurationPanel = decoder.getBoolean(9),
            minTarnhelmSdkVersion = decoder.getInt(10),
            minAndroidSdkVersion = decoder.getInt(11),
            regexes = decoder.getStringList(12)!!
        )
    }

    override fun encode(encoder: PackEncoder, target: ExtensionRecord) {
        encoder.putBoolean(0, target.enabled)
        encoder.putString(1, target.entryClassName)
        encoder.putString(2, target.id)
        encoder.putString(3, target.author)
        encoder.putString(4, target.name)
        encoder.putString(5, target.description)
        encoder.putString(6, target.extensionURL)
        encoder.putInt(7, target.versionCode)
        encoder.putString(8, target.versionName)
        encoder.putBoolean(9, target.hasConfigurationPanel)
        encoder.putInt(10, target.minTarnhelmSdkVersion)
        encoder.putInt(11, target.minAndroidSdkVersion)
        encoder.putStringList(12, target.regexes)
    }

}