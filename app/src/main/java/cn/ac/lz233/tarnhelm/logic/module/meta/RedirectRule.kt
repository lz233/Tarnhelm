package cn.ac.lz233.tarnhelm.logic.module.meta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RedirectRule(
    @PrimaryKey val id: Int,
    val description: String,
    val domain: String,
    val author: String,
    val sourceType: Int, // 0:manual 1:paste 2:??
    val enabled: Boolean,
)