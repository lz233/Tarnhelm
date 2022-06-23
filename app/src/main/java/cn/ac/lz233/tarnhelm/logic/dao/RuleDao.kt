package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg rules: Rule)

    @Query("SELECT * FROM rule")
    fun getAll():List<Rule>

    @Delete
    fun delete(rule: Rule)
}