package com.videochat.domain.model

data class UserDomainModel(
    var userId: String,
    val userEmail: String,
    var userName: String,
    var clientUID: Int
)
