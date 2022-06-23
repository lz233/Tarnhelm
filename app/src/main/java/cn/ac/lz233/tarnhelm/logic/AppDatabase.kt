package cn.ac.lz233.tarnhelm.logic

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.ac.lz233.tarnhelm.logic.dao.RuleDao
import cn.ac.lz233.tarnhelm.logic.module.meta.Rule

@Database(entities = [Rule::class], version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun ruleDao():RuleDao
}