package com.videochat.domain.usecase.app

import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.domain.repository.UserCacheRepository
import javax.inject.Inject

class ClearUserCacheUseCase @Inject constructor(
    private val userCacheRepository: UserCacheRepository
): UseCase {
    suspend fun execute() {
        userCacheRepository.deleteAll()
    }
}
