package cn.ac.lz233.tarnhelm.logic.module.meta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rule(
    @PrimaryKey val id:Int,
    val description: String,
    val regexArray: String,
    val replaceArray: String,
    val author: String
)