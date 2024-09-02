package com.videochat.domain.model.session

import com.google.firebase.Timestamp

data class SessionModel(
    val sessionId: String,
    val callerId: String,
    val receiverId: String,
    val duration: Int,
    val date: Timestamp
)
