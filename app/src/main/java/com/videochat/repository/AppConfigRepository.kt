package com.videochat.repository

import com.videochat.data.dao.AppConfigDao
import com.videochat.domain.entity.config.AppConfigEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppConfigRepository(private val appConfigDao: AppConfigDao) {

    suspend fun getAppConfig(): AppConfigEntity {
        return withContext(Dispatchers.IO) {
            appConfigDao.getConfig(1)
        }
    }
    suspend fun updateAppConfig(config: AppConfigEntity) {
        withContext(Dispatchers.IO) {
            appConfigDao.updateConfig(config)
        }
    }
    suspend fun insertAppConfig(config: AppConfigEntity) {
        withContext(Dispatchers.IO) {
            appConfigDao.insertConfig(config)
        }
    }

    suspend fun setRememberMe(rememberMe: Boolean) {
        val appConfig = AppConfigEntity(id = 1, rememberMe = rememberMe)
        updateAppConfig(appConfig)
    }

    suspend fun getConfig(): AppConfigEntity {
        val appConfig = getAppConfig()
        return appConfig
    }

}
