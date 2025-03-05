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
import cn.ac.lz233.tarnhelm.extension.exception.ConfigurationPanelException
import cn.ac.lz233.tarnhelm.extension.storage.ExtensionOwnStorage
import cn.ac.lz233.tarnhelm.extension.storage.ExtensionRecordStorage
import cn.ac.lz233.tarnhelm.util.ktx.getExtPath
import java.nio.file.Files
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
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
            Log.d("ExtensionManager", "Starting enabled extension (id:${ext.id})")
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
        if (extensionRecordStorage.get(extensionRecord.id) != null) {
            stopExtension(extensionRecord)
            extensionRecordStorage.remove(extensionRecord.id)
        }
        extensionRecordStorage.add(extensionRecord)
        Log.d("ExtensionManager", "Extension (id:${extensionRecord.id}) registered")
        Log.d("ExtensionManager", "Current installed extension: ${extensionRecordStorage.getAll().size}")
        val service = createExtensionService(extensionRecord)
        thread { runCatching { service.onExtInstall() } }
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

    @Throws(ConfigurationPanelException::class)
    fun startExtensionConfigurationPanel(extRecord: ExtensionRecord, activity: Activity) {
        if (!extRecord.hasConfigurationPanel) {
            throw RuntimeException("Extension (id=${extRecord.id}) has no configuration panel as mentioned")
        }
        val extService = runningExtMap[extRecord] ?: createExtensionService(extRecord)
        try {
            val panelImpl = extService as IExtConfigurationPanel
            val view = panelImpl.onRequestConfigurationPanel(createRestrictedAppContext(), ExtensionOwnStorage(extRecord.getExtPath(context)))
            AlertDialog.Builder(activity)
                .setView(view)
                .show()
        } catch (e: ClassCastException) {
            throw ConfigurationPanelException("Extension (id=${extRecord.id}) does not implement IExtConfigurationPanel", e)
        } catch (e: Exception) {
            throw ConfigurationPanelException("Unknown error occurred", e)
        }
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

    @Throws(RuntimeException::class)
    fun uninstallExtension(extRecord: ExtensionRecord) {
        Log.d("ExtensionManager", "Uninstalling extension (id:${extRecord.id})")
        val ext = extensionRecordStorage.get(extRecord.id) ?: throw RuntimeException("???")
        Log.d("ExtensionManager", "Extension (id:$extRecord.id) found")
        val extService = if (runningExtMap.any { it.key.id == extRecord.id }) {
            runningExtMap.filter { it.key.id == extRecord.id }.values.first()
        } else {
            createExtensionService(extensionRecordStorage.get(extRecord.id)!!)
        }
        extService.onExtUninstall()
        stopExtension(ext)
        Log.d("ExtensionManager", "Extension (id:$extRecord.id) stopped")
        // delete extension dir and all files
        Files.walk(Path(ext.getExtPath(context)))
            .sorted(Comparator.reverseOrder())
            .forEach {
                Files.delete(it)
                Log.d("ExtensionManager", "Deleted file: $it")
            }
        extensionRecordStorage.remove(extRecord.id)
    }

    @Throws(RuntimeException::class)
    fun enableExtension(extRecord: ExtensionRecord) {
        val ext = extensionRecordStorage.get(extRecord.id) ?: throw RuntimeException("???")
        if (ext.enabled) return
        ext.enabled = true
        extensionRecordStorage.modify(extRecord.id, ext)
        startExtension(ext)
    }

    @Throws(RuntimeException::class)
    fun disableExtension(extRecord: ExtensionRecord) {
        val ext = extensionRecordStorage.get(extRecord.id) ?: throw RuntimeException("???")
        if (!ext.enabled) return
        ext.enabled = false
        extensionRecordStorage.modify(extRecord.id, ext)
        stopExtension(ext)
    }

    @Throws(RuntimeException::class)
    suspend fun requestHandleString(extRecord: ExtensionRecord, charSequence: CharSequence) = suspendCoroutine<String> {
        val service = runningExtMap.getOrDefault(extRecord, null)
        if (service == null) {
            it.resumeWithException(RuntimeException("Extension (id=${extRecord.id}) is not running"))
        }
        runCatching { service!!.handleLoadString(charSequence) }
            .onFailure { e -> it.resumeWithException(e) }
            .onSuccess { result -> it.resume(result) }
    }

    suspend fun requestCheckUpdate(extRecord: ExtensionRecord) = suspendCoroutine<String> {
        val service = runningExtMap.getOrDefault(extRecord, null)
        if (service == null) {
            it.resumeWithException(RuntimeException("Extension (id=${extRecord.id}) is not running"))
        }
        runCatching { service!!.checkUpdate() }
            .onFailure { e -> it.resumeWithException(e) }
            .onSuccess { result -> it.resume(result) }
    }

}


