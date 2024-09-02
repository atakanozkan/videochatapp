package com.videochat.domain.entity.user

data class FirestoreUserEntity(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val passwordHash: String = "",
    val salt: String = "",
    val clientUID: Int = 0
)
