package cn.ac.lz233.tarnhelm.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import cn.ac.lz233.tarnhelm.BuildConfig
import cn.ac.lz233.tarnhelm.extension.api.ExtContext
import cn.ac.lz233.tarnhelm.extension.api.ExtService
import cn.ac.lz233.tarnhelm.extension.api.ExtSharedPreferences
import cn.ac.lz233.tarnhelm.extension.api.IExtConfigurationPanel
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt
import cn.ac.lz233.tarnhelm.extension.storage.ExtensionOwnStorage
import cn.ac.lz233.tarnhelm.extension.storage.ExtensionRecordStorage
import cn.ac.lz233.tarnhelm.util.ktx.getExtPath
import java.nio.file.Files
import kotlin.io.path.Path

class ExtensionManagerService(private val context: Context) {

    private var isInitialized = false
    private lateinit var extensionRecordStorage: ExtensionRecordStorage
    private val runningExtMap = mutableMapOf<ExtensionRecord, ExtService>()
    private val createExtensionServiceMethod = ITarnhelmExt::class.java.getDeclaredMethod("createExtensionService", ExtContext::class.java)
    val extensionClassLoaderParent = ExtensionClassLoader()

    companion object {
        const val EXT_SDK_VERSION = 1
    }

    fun init() {
        if (isInitialized) return
        extensionRecordStorage = ExtensionRecordStorage(context)
        startEnabledExtensions()
        isInitialized = true
    }

    private fun startEnabledExtensions() {
        extensionRecordStorage.getAll().filter { it.enabled }.forEach { ext ->
            runCatching { startExtension(ext) }
                .onFailure { throw RuntimeException("Failed to start extension (id=${ext.id})", it) }
        }
    }

    private fun startExtension(extensionRecord: ExtensionRecord) {
        if (runningExtMap.containsKey(extensionRecord)) return
        val extService = createExtensionService(extensionRecord)
        runningExtMap[extensionRecord] = extService
    }

    private fun stopExtension(extensionRecord: ExtensionRecord) {
        runningExtMap.remove(extensionRecord) ?: return
    }

    fun registerExtension(extensionRecord: ExtensionRecord) {
        if (extensionRecordStorage.getAll().any { it.id == extensionRecord.id }) {
            stopExtension(extensionRecord)
            extensionRecordStorage.remove(extensionRecord.id)
        }
        extensionRecordStorage.add(extensionRecord)
        Log.d("ExtensionManager", "Extension (id:${extensionRecord.id}) registered")
        Log.d("ExtensionManager", "Current installed extension: ${extensionRecordStorage.getAll().size}")
        val service = createExtensionService(extensionRecord)
        service.onExtInstall()
    }

    private fun loadExtension(extensionRecord: ExtensionRecord) = MemoryDexLoader.createClassLoaderWithDex(Files.readAllBytes(Path(extensionRecord.getExtPath(context) + "ext.dex")), extensionClassLoaderParent)

    private fun createExtensionService(extensionRecord: ExtensionRecord) : ExtService {
        val extClassLoader = loadExtension(extensionRecord)
        val entryClazz = extClassLoader.loadClass(extensionRecord.entryClassName)
        val extObj = entryClazz.newInstance() as ITarnhelmExt
        return createExtensionServiceMethod.invoke(extObj, object : ExtContext {
            private val ownSP = ExtensionOwnStorage(extensionRecord.getExtPath(context))
            override fun tarnhelmSdkVersion(): Int = EXT_SDK_VERSION
            override fun getSharedPreferences(): ExtSharedPreferences = ownSP
        }) as ExtService
    }

    fun startExtensionConfigurationPanel(extensionId: String, activity: Activity) {
        val extensionRecord = extensionRecordStorage.get(extensionId) ?: return
        if (!extensionRecord.hasConfigurationPanel) {
            throw RuntimeException("Extension (id=${extensionRecord.id}) has no configuration panel as mentioned")
        }
        val extService = runningExtMap[extensionRecord] ?: createExtensionService(extensionRecord)
        val panelImpl = extService as IExtConfigurationPanel
        val view = panelImpl.onRequestConfigurationPanel(createRestrictedAppContext(), ExtensionOwnStorage(extensionRecord.getExtPath(context)))
        AlertDialog.Builder(activity)
            .setView(view)
            .show()
    }

    private fun createRestrictedAppContext(): Context {
        return context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_RESTRICTED)
    }

    fun getInstalledExtensions(): List<ExtensionRecord> {
        return extensionRecordStorage.getAll()
    }

    fun getRunningExtensions(): List<ExtensionRecord> {
        return runningExtMap.keys.toList()
    }

    fun uninstallExtension(extId: String) {
        Log.d("ExtensionManager", "Uninstalling extension (id:$extId)")
        val ext = extensionRecordStorage.getAll().find { it.id == extId } ?: return
        Log.d("ExtensionManager", "Extension (id:$extId) found")
        val extService = if (runningExtMap.any { it.key.id == extId }) {
            runningExtMap.filter { it.key.id == extId }.values.first()
        } else {
            createExtensionService(extensionRecordStorage.get(extId)!!)
        }
        extService.onExtUninstall()
        stopExtension(ext)
        Log.d("ExtensionManager", "Extension (id:$extId) stopped")
        // delete extension dir and all files
        Files.walk(Path(ext.getExtPath(context)))
            .sorted(Comparator.reverseOrder())
            .forEach {
                Files.delete(it)
                Log.d("ExtensionManager", "Deleted file: $it")
            }
        extensionRecordStorage.remove(extId)
    }

    fun enableExtension(extId: String) {
        val ext = extensionRecordStorage.getAll().find { it.id == extId } ?: return
        if (ext.enabled) return
        ext.enabled = true
        extensionRecordStorage.modify(extId, ext)
        startExtension(ext)
    }

    fun disableExtension(extId: String) {
        val ext = extensionRecordStorage.getAll().find { it.id == extId } ?: return
        if (!ext.enabled) return
        ext.enabled = false
        extensionRecordStorage.modify(extId, ext)
        stopExtension(ext)
    }

}


