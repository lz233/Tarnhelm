package cn.ac.lz233.tarnhelm.logic.module.meta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Extension(
    @PrimaryKey val packageName: String,
    val name: String,
    val description: String,
    val regexArray: String,
    val author: String,
    val url: String,
    val enabled: Boolean,
)