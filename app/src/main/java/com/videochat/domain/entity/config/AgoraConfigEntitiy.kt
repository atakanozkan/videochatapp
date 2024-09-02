package com.videochat.domain.entity.config

data class AgoraConfigEntity(
    val agoraAppId: String = "",
    val appCertificate: String = "",
    val chatAppKey: String = "",
    val chatClient: String = "",
    val chatClientToken: String = "",
)
