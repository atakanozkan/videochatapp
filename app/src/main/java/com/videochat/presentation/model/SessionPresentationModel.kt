package com.videochat.presentation.model

import com.google.firebase.Timestamp

data class SessionPresentationModel(
    val sessionId: String,
    val callerId: String,
    val receiverId: String,
    val duration: Int,
    val date: Timestamp
)
