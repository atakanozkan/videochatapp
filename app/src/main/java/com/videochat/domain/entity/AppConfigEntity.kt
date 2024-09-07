package com.videochat.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    val rememberMe: Boolean
)