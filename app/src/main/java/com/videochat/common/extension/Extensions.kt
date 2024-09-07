package com.videochat.common.extension

import com.videochat.domain.entity.AgoraConfigEntity
import com.videochat.domain.entity.SessionEntity
import com.videochat.domain.entity.FirestoreUserEntity
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.model.AgoraConfigDomainModel
import com.videochat.domain.model.SessionDomainModel
import com.videochat.domain.model.UserDomainModel
import com.videochat.presentation.model.SessionPresentationModel
import com.videochat.presentation.model.UserPresentationModel
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger

fun FirestoreUserEntity.toDomainModel(): UserDomainModel {
    return UserDomainModel(
        userId = this.userId,
        userName = this.userName,
        userEmail = this.userEmail,
        clientUID = this.clientUID
    )
}

fun UserDomainModel.toPresentationModel(): UserPresentationModel {
    return UserPresentationModel(
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

fun AgoraConfigEntity.toDomainModel(): AgoraConfigDomainModel {
    return AgoraConfigDomainModel(
        agoraAppId = this.agoraAppId,
        appCertificate = this.appCertificate,
        chatAppKey = this.chatAppKey,
        chatClient = this.chatClient,
        chatClientToken = this.chatClientToken
    )
}

fun SessionEntity.toDomainModel(): SessionDomainModel = SessionDomainModel(
    sessionId = sessionId,
    callerId = callerId,
    receiverId = receiverId,
    duration = duration,
    date = date
)

fun SessionDomainModel.toPresentationModel(): SessionPresentationModel = SessionPresentationModel(
    sessionId = sessionId,
    callerId = callerId,
    receiverId = receiverId,
    duration = duration,
    date = date
)

fun com.videochat.domain.model.AuthenticationState.toPresentationModel(): com.videochat.presentation.model.AuthenticationState {
    return when (this) {
        com.videochat.domain.model.AuthenticationState.Authenticated -> com.videochat.presentation.model.AuthenticationState.Authenticated
        com.videochat.domain.model.AuthenticationState.Unauthenticated -> com.videochat.presentation.model.AuthenticationState.Unauthenticated
        com.videochat.domain.model.AuthenticationState.Authenticating -> com.videochat.presentation.model.AuthenticationState.Authenticating
        com.videochat.domain.model.AuthenticationState.Failed -> com.videochat.presentation.model.AuthenticationState.Failed
    }
}


fun String.fromBase64String(): ByteArray = Base64.getDecoder().decode(this.trim())

object UniqueIdGenerator {
    private val counter = AtomicInteger(0)

    fun generateUniqueId(): Int {
        val currentTime = System.currentTimeMillis()
        val sequence = com.videochat.common.extension.UniqueIdGenerator.counter.incrementAndGet()
        val combined = "$currentTime-$sequence"
        return combined.hashCode()
    }
}