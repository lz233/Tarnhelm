package cn.ac.lz233.tarnhelm.logic

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.ac.lz233.tarnhelm.logic.dao.ExtensionDao
import cn.ac.lz233.tarnhelm.logic.dao.ParameterRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.RedirectRuleDao
import cn.ac.lz233.tarnhelm.logic.dao.RegexRuleDao
import cn.ac.lz233.tarnhelm.logic.module.meta.Extension
import cn.ac.lz233.tarnhelm.logic.module.meta.ParameterRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RedirectRule
import cn.ac.lz233.tarnhelm.logic.module.meta.RegexRule

@Database(entities = [RegexRule::class, ParameterRule::class, RedirectRule::class, Extension::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun regexRuleDao(): RegexRuleDao
    abstract fun parameterRuleDao(): ParameterRuleDao
    abstract fun redirectRuleDao(): RedirectRuleDao
    abstract fun extensionDao(): ExtensionDao
}