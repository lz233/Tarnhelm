package cn.ac.lz233.tarnhelm.extension.storage

import cn.ac.lz233.tarnhelm.extension.api.ExtSharedPreferences
import io.fastkv.FastKV

class ExtensionOwnStorage(dirPath: String) : ExtSharedPreferences {

    private val kv: FastKV = FastKV.Builder(dirPath, "sp.fastkv").build()

    override fun getAll(): MutableMap<String, *> = kv.all

    override fun getString(var1: String?, var2: String?): String? = kv.getString(var1, var2)

    override fun getStringSet(var1: String?, var2: MutableSet<String>?): MutableSet<String>? = kv.getStringSet(var1, var2)

    override fun getInt(var1: String?, var2: Int): Int = kv.getInt(var1, var2)

    override fun getLong(var1: String?, var2: Long): Long = kv.getLong(var1, var2)

    override fun getFloat(var1: String?, var2: Float): Float = kv.getFloat(var1, var2)

    override fun getBoolean(var1: String?, var2: Boolean): Boolean = kv.getBoolean(var1, var2)

    override fun contains(var1: String?): Boolean = kv.contains(var1)

    override fun edit(): ExtSharedPreferences.Editor = object : ExtSharedPreferences.Editor {

        override fun putString(var1: String?, var2: String?): ExtSharedPreferences.Editor {
            kv.putString(var1, var2)
            return this
        }

        override fun putStringSet(var1: String?, var2: MutableSet<String>?): ExtSharedPreferences.Editor {
            kv.putStringSet(var1, var2)
            return this
        }

        override fun putInt(var1: String?, var2: Int): ExtSharedPreferences.Editor {
            kv.putInt(var1, var2)
            return this
        }

        override fun putLong(var1: String?, var2: Long): ExtSharedPreferences.Editor {
            kv.putLong(var1, var2)
            return this
        }

        override fun putFloat(var1: String?, var2: Float): ExtSharedPreferences.Editor {
            kv.putFloat(var1, var2)
            return this
        }

        override fun putBoolean(var1: String?, var2: Boolean): ExtSharedPreferences.Editor {
            kv.putBoolean(var1, var2)
            return this
        }

        override fun remove(var1: String?): ExtSharedPreferences.Editor {
            kv.remove(var1)
            return this
        }

        override fun clear(): ExtSharedPreferences.Editor {
            kv.clear()
            return this
        }

        override fun commit(): Boolean {
            return kv.commit()
        }

        override fun apply() {
            kv.apply()
        }
    }
}