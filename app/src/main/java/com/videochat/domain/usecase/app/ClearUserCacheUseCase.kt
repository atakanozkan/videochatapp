package com.videochat.domain.usecase.app

import com.videochat.domain.repository.UserCacheRepository
import javax.inject.Inject

class ClearUserCacheUseCase @Inject constructor(
    private val userCacheRepository: UserCacheRepository
) {
    suspend fun execute() {
        userCacheRepository.deleteAll()
    }
}
