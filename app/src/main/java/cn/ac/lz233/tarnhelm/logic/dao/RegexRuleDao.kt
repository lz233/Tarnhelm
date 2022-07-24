package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule

@Dao
interface RegexRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg regexRules: RegexRule)

    @Query("SELECT * FROM regexrule")
    fun getAll(): MutableList<RegexRule>

    @Query("SELECT count(*) FROM regexrule")
    fun getCount(): Int

    @Query("SELECT max(id) FROM regexrule")
    fun getMaxId(): Int

    @Delete
    fun delete(regexRule: RegexRule)
}