package cn.ac.lz233.tarnhelm.extension

import android.content.Context

class ExtensionClassLoader: ClassLoader(sBootClassLoader) {

    companion object {
        private val sBootClassLoader = Context::class.java.classLoader
        private val sHostClassLoader = ExtensionClassLoader::class.java.classLoader
        private val whitelist = arrayOf(
            "cn.ac.lz233.tarnhelm.extension.api.",
            "cn.ac.lz233.tarnhelm.extension.storage.ExtensionOwnStorage"
        )
    }

    override fun findLibrary(name: String?): String {
        throw RuntimeException("findLibrary is not supported")
    }

    override fun findClass(name: String?): Class<*> {
        try {
            return sBootClassLoader.loadClass(name)
        } catch (ignored: ClassNotFoundException) {}
        if (whitelist.any { name?.startsWith(it) == true }) {
            return sHostClassLoader.loadClass(name)
        }
        return super.findClass(name)
    }
}