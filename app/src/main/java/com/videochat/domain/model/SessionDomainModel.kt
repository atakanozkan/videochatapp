package com.videochat.domain.model

import com.google.firebase.Timestamp

data class SessionDomainModel(
    val sessionId: String,
    val callerId: String,
    val receiverId: String,
    val duration: Int,
    val date: Timestamp
)
