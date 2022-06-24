package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg rules: Rule)

    @Query("SELECT * FROM rule")
    fun getAll():MutableList<Rule>

    @Query("SELECT count(*) FROM rule")
    fun getCount():Int

    @Delete
    fun delete(rule: Rule)
}