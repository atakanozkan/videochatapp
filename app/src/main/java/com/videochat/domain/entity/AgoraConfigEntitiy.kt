package com.videochat.domain.entity

data class AgoraConfigEntity(
    val agoraAppId: String = "",
    val appCertificate: String = "",
    val chatAppKey: String = "",
    val chatClient: String = "",
    val chatClientToken: String = "",
)
