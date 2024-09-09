package com.videochat.di

import com.videochat.domain.usecase.app.LoadAppConfigUseCase
import com.videochat.domain.usecase.app.SetAppConfigUseCase
import com.videochat.domain.usecase.source.GetAgoraCredentialsUseCase
import com.videochat.presentation.viewmodel.AppConfigViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Provides
    fun provideAppConfigViewModel(
        setAppConfigUseCase: SetAppConfigUseCase,
        loadAppConfigUseCase: LoadAppConfigUseCase,
        getAgoraCredentialsUseCase: GetAgoraCredentialsUseCase,
    ): AppConfigViewModel = AppConfigViewModel(setAppConfigUseCase,loadAppConfigUseCase,getAgoraCredentialsUseCase)
}
