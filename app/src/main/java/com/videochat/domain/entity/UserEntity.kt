package com.videochat.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false) val userId: String,
    val userName: String,
    val userEmail: String,
    val passwordHash: String,
    val salt: ByteArray,
    val clientUID: Int
)