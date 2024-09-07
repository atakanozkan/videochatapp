package com.videochat.domain.entity

import com.google.firebase.Timestamp

data class SessionEntity(
    val sessionId: String = "",
    val callerId: String = "",
    val receiverId: String = "",
    val duration: Int = 0,
    val date: Timestamp = Timestamp.now()
)

