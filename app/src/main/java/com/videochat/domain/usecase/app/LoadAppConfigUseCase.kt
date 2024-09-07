package com.videochat.domain.usecase.app


import com.videochat.domain.entity.AppConfigEntity
import com.videochat.domain.repository.AppConfigRepository
import javax.inject.Inject

class LoadAppConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) {
    suspend fun execute(): AppConfigEntity {
        var appConfig = appConfigRepository.getConfig()
        if (appConfig == null) {
            appConfig = AppConfigEntity(rememberMe = false)
            appConfigRepository.insertAppConfig(appConfig)
        }
        return appConfig
    }
}