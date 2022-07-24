package cn.ac.lz233.tarnhelm.logic

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.ac.lz233.tarnhelm.logic.dao.ParameterRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.RegexRuleDao
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule

@Database(entities = [RegexRule::class, ParameterRule::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun regexRuleDao(): RegexRuleDao
    abstract fun parameterRuleDao(): ParameterRuleDao
}