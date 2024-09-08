package com.videochat.domain.usecase.app

import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.domain.repository.AppConfigRepository
import javax.inject.Inject

class SetAppConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository
): UseCase {
    suspend fun execute(rememberMe: Boolean) {
        appConfigRepository.setRememberMe(rememberMe)
    }
}