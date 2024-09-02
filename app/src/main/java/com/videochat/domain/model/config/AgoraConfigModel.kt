package com.videochat.domain.model.config

data class AgoraConfigModel (
    val agoraAppId: String = "",
    val appCertificate: String = "",
    val chatAppKey: String = "",
    val chatClient: String = "",
    val chatClientToken: String = "",
)