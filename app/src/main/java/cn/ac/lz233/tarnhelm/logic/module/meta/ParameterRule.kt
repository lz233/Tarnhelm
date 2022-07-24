package cn.ac.lz233.tarnhelm.logic.module.meta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ParameterRule(
    @PrimaryKey val id: Int,
    val description: String,
    val domain: String,
    val mode: Int, // 0:whiteList 1:blackList
    val parametersArray: String,
    val author: String,
    val sourceType: Int, // 0:manual 1:paste 2:git
    val enabled: Boolean,
)