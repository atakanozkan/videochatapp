package com.videochat.domain.usecase.app

import com.videochat.common.extension.HashPassword
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.repository.UserCacheRepository
import javax.inject.Inject
class UpdateUserInCacheUseCase @Inject constructor(
    private val userCacheRepository: UserCacheRepository
) {
    suspend fun execute(userId: String, userName: String, email: String, password: String, clientUID: Int) {
        val salt = HashPassword.generateSalt()
        val hashedPassword = HashPassword.hashPassword(password, salt)
        userCacheRepository.updateUser(
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
