package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule

@Dao
interface ParameterRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg parameterRule: ParameterRule)

    @Query("SELECT * FROM parameterrule")
    fun getAll(): MutableList<ParameterRule>

    @Query("SELECT count(*) FROM parameterrule")
    fun getCount(): Int

    @Query("SELECT max(id) FROM parameterrule")
    fun getMaxId(): Int

    @Delete
    fun delete(parameterRule: ParameterRule)
}