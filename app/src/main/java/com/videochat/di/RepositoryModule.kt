package com.videochat.di

import com.videochat.data.dao.AppConfigDao
import com.videochat.data.dao.UserDao
import com.videochat.domain.repository.AppConfigRepository
import com.videochat.domain.repository.UserCacheRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideUserRepository(userDao: UserDao): UserCacheRepository = UserCacheRepository(userDao)


    @Provides
    fun provideAppConfigRepository(appConfigDao: AppConfigDao): AppConfigRepository = AppConfigRepository(appConfigDao)
}