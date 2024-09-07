package com.videochat.domain.usecase.app

import com.videochat.domain.repository.AppConfigRepository
import javax.inject.Inject

class SetAppConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) {
    suspend fun execute(rememberMe: Boolean) {
        appConfigRepository.setRememberMe(rememberMe)
    }
}