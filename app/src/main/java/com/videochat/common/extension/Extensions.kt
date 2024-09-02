package com.videochat.common.extension

import com.videochat.domain.entity.config.AgoraConfigEntity
import com.videochat.domain.entity.session.SessionEntity
import com.videochat.domain.entity.user.FirestoreUserEntity
import com.videochat.domain.entity.user.UserEntity
import com.videochat.domain.model.config.AgoraConfigModel
import com.videochat.domain.model.session.SessionModel
import com.videochat.domain.model.user.UserModel
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger

fun FirestoreUserEntity.toDomainModel(): UserModel {
    return UserModel(
        userId = this.userId,
        userName = this.userName,
        userEmail = this.userEmail,
        clientUID = this.clientUID
    )
}

fun FirestoreUserEntity.toUserEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        passwordHash = passwordHash,
        salt = salt.fromBase64String(),
        clientUID = clientUID
    )
}

fun AgoraConfigEntity.toDomainModel(): AgoraConfigModel {
    return AgoraConfigModel(
        agoraAppId = this.agoraAppId,
        appCertificate = this.appCertificate,
        chatAppKey = this.chatAppKey,
        chatClient = this.chatClient,
        chatClientToken = this.chatClientToken
    )
}

fun SessionEntity.toDomainModel(): SessionModel = SessionModel(
    sessionId = sessionId,
    callerId = callerId,
    receiverId = receiverId,
    duration = duration,
    date = date
)

fun String.fromBase64String(): ByteArray = Base64.getDecoder().decode(this.trim())

object UniqueIdGenerator {
    private val counter = AtomicInteger(0)

    fun generateUniqueId(): Int {
        val currentTime = System.currentTimeMillis()
        val sequence = counter.incrementAndGet()
        val combined = "$currentTime-$sequence"
        return combined.hashCode()
    }
}