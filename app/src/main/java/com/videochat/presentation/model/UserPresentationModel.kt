package com.videochat.presentation.model

data class UserPresentationModel(
    var userId: String,
    val userEmail: String,
    var userName: String,
    var clientUID: Int
)
