package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.*
import cn.ac.lz233.tarnhelm.logic.module.meta.RedirectRule

@Dao
interface RedirectRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg redirectRule: RedirectRule)

    @Query("SELECT * FROM redirectrule")
    fun getAll(): MutableList<RedirectRule>

    @Query("SELECT count(*) FROM redirectrule")
    fun getCount(): Int

    @Query("SELECT max(id) FROM redirectrule")
    fun getMaxId(): Int

    @Delete
    fun delete(redirectRule: RedirectRule)
}