package com.videochat.domain.usecase.app

import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.common.extension.HashPassword
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.repository.UserCacheRepository
import javax.inject.Inject

class SaveUserToCacheUseCase @Inject constructor(
    private val userCacheRepository: UserCacheRepository
): UseCase {
    suspend fun execute(userId: String, userName: String, email: String, password: String, salt: ByteArray, clientUID: Int) {
        val hashedPassword = HashPassword.hashPassword(password, salt)
        userCacheRepository.insertUser(
            UserEntity(
                userId = userId,
                userName = userName,
                userEmail = email,
                passwordHash = hashedPassword,
                salt = salt,
                clientUID = clientUID
            )
        )
    }
}
