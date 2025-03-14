package cn.ac.lz233.tarnhelm.extension

import android.app.Activity
import android.util.Log
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt
import cn.ac.lz233.tarnhelm.extension.exception.ConfigurationPanelException
import cn.ac.lz233.tarnhelm.extension.exception.InvalidExtensionException
import cn.ac.lz233.tarnhelm.util.ktx.getExtPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.jvm.Throws

object ExtensionManager {

    private val mExtensionManagerService by lazy { ExtensionManagerService(App.context) }

    fun init() {
        mExtensionManagerService.init()
        Log.d("ExtensionManager", "Total installed extension: ${mExtensionManagerService.getInstalledExtensions().size}")
    }

    fun getInstalledExtensions() = mExtensionManagerService.getInstalledExtensions()

    fun getRunningExtensions() = mExtensionManagerService.getRunningExtensions()

    @Throws(InvalidExtensionException::class)
    suspend fun installExtension(stream: InputStream) = withContext(Dispatchers.IO) {
        // load dex in memory
        val byteArrays = stream.readBytes()
        stream.close()
        val classLoader = MemoryDexLoader.createClassLoaderWithDex(byteArrays, mExtensionManagerService.extensionClassLoaderParent)
        val extInfo = findExtensionInfo(classLoader)!!
        Log.d("ExtensionManager", "Extension info found: $extInfo, extension id: ${extInfo.id()}")

        // check extension id
        val extId = extInfo.id()
        val range = CharRange('a', 'z') + CharRange('A', 'Z') + CharRange('0', '9') + '.'
        if (extId.any { it !in range } || extId.length > 255) {
            throw InvalidExtensionException("Invalid extension id(length:${extId.length}): $extId")
        }

        // check whether extension is already installed
        if (mExtensionManagerService.getInstalledExtensions().any { it.id == extId }) {
            Log.d("ExtensionManager", "Warning: Extension (id:$extId) is already installed")
        }

        // install extension to extension dir
        val extPath = File(extInfo.getExtPath(App.context), "ext.dex")
        Log.d("ExtensionManager", "Installing extension (id:$extId) to $extPath")

        extPath.parentFile?.mkdirs()
        extPath.writeBytes(byteArrays)

        mExtensionManagerService.registerExtension(ExtensionRecord.fromExtInfo(extInfo, getExtensionEntry(classLoader)))
        Log.d("ExtensionManager", "Extension (id:$extId) installed successfully")
    }

    fun toMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun findExtensionInfo(classLoader: ClassLoader): ITarnhelmExt.ExtInfo? {
        try {
            val realEntry = classLoader.loadClass(getExtensionEntry(classLoader)).newInstance()
            return (realEntry as ITarnhelmExt).extensionInfo()
        } catch (e: Exception) {
            throw InvalidExtensionException("Loading invalid extension", e)
        }
    }

    private fun getExtensionEntry(classLoader: ClassLoader): String {
        try {
            val entryClazz = classLoader.loadClass("TarnhelmExtEntry")
            val field = entryClazz.getDeclaredField("entryClassName")
            return field.get(null) as String
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("Invalid extension, no entry class found")
        } catch (e: Exception) {
            throw RuntimeException("Invalid extension, ${e.message}")
        }
    }

    @Throws(RuntimeException::class)
    fun uninstallExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.uninstallExtension(extRecord)
    }

    @Throws(RuntimeException::class)
    fun enableExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.enableExtension(extRecord)
    }

    @Throws(RuntimeException::class)
    fun disableExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.disableExtension(extRecord)
    }

    @Throws(ConfigurationPanelException::class)
    fun startExtensionConfigurationPanel(extRecord: ExtensionRecord, activity: Activity) {
        mExtensionManagerService.startExtensionConfigurationPanel(extRecord, activity)
    }

    suspend fun requestHandleString(extRecord: ExtensionRecord, charSequence: CharSequence) = withContext(Dispatchers.IO) {
        return@withContext mExtensionManagerService.requestHandleString(extRecord, charSequence)
    }

    suspend fun requestCheckUpdate(extRecord: ExtensionRecord) = withContext(Dispatchers.IO) {
        return@withContext mExtensionManagerService.requestCheckUpdate(extRecord)
    }

}