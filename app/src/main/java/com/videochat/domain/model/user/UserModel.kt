package com.videochat.domain.model.user

data class UserModel(
    var userId: String,
    val userEmail: String,
    var userName: String,
    var clientUID: Int
)
