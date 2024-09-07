package com.videochat.domain.model

data class AgoraConfigDomainModel (
    val agoraAppId: String = "",
    val appCertificate: String = "",
    val chatAppKey: String = "",
    val chatClient: String = "",
    val chatClientToken: String = "",
)