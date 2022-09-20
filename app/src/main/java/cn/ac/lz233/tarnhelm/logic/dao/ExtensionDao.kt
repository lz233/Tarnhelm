package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.Extension

@Dao
interface ExtensionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg extensions: Extension)

    @Query("SELECT * FROM extension")
    fun getAll(): MutableList<Extension>

    @Query("SELECT count(*) FROM extension")
    fun getCount(): Int

    @Delete
    fun delete(extension: Extension)
}