package cn.ac.lz233.tarnhelm.extension

import android.content.Context
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt.ExtInfo
import io.fastkv.FastKV
import io.fastkv.interfaces.FastEncoder
import io.packable.PackDecoder
import io.packable.PackEncoder

class ExtensionRecordStorage(context: Context) {

    companion object {

        const val KEY = "extensions"

        private val extensionRecordListEncoder = object : FastEncoder<List<ExtensionRecord>> {

            override fun tag(): String = "ExtInfoMap"

            override fun decode(bytes: ByteArray, offset: Int, length: Int): List<ExtensionRecord> {
                return PackDecoder(bytes, offset, length).getObjectList(0, ExtensionRecordAdapter) ?: emptyList()
            }

            override fun encode(obj: List<ExtensionRecord>): ByteArray {
                return PackEncoder().putObjectList(0, obj, ExtensionRecordAdapter).toBytes()
            }

        }

    }

    private val theList = arrayListOf<ExtensionRecord>()

    private val kv: FastKV

    init {
        kv = FastKV
            .Builder("${context.filesDir.absolutePath}/extensions", "config.fastkv")
            .encoder(arrayOf(extensionRecordListEncoder))
            .build()
        val data : List<ExtensionRecord> = kv.getObject(KEY) ?: emptyList()
        theList.addAll(data)
    }

    val size: Int
        get() {
            synchronized(theList) {
                return theList.size
            }
        }

    fun add(extensionRecord: ExtensionRecord) {
        synchronized(theList) {
            theList.add(extensionRecord)
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun add(extInfo: ExtInfo, entryClassName: String) {
        synchronized(theList) {
            theList.add(ExtensionRecord.fromExtInfo(extInfo, entryClassName))
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun remove(extensionRecord: ExtensionRecord) {
        synchronized(theList) {
            theList.remove(extensionRecord)
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun remove(id: String) {
        synchronized(theList) {
            theList.removeIf { it.id == id }
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun contains(extensionRecord: ExtensionRecord): Boolean {
        synchronized(theList) {
            return theList.contains(extensionRecord)
        }
    }

    fun contains(id: String): Boolean {
        synchronized(theList) {
            return theList.any { it.id == id }
        }
    }

    fun clear() {
        synchronized(theList) {
            theList.clear()
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun modify(id: String, extensionRecord: ExtensionRecord) {
        synchronized(theList) {
            theList.removeIf { it.id == id }
            theList.add(extensionRecord)
            kv.putObject(KEY, theList, extensionRecordListEncoder)
        }
    }

    fun getAll(): List<ExtensionRecord> {
        synchronized(theList) {
            return theList.toList()
        }
    }

    fun get(id: String): ExtensionRecord? {
        synchronized(theList) {
            return theList.firstOrNull { it.id == id }
        }
    }

}