package com.videochat.di

import com.videochat.domain.usecase.app.AuthenticateUserUseCase
import com.videochat.domain.usecase.app.AutoAuthenticateUseCase
import com.videochat.domain.usecase.app.RegisterUserUseCase
import com.videochat.presentation.viewmodel.VideoChatLoginViewModel
import com.videochat.presentation.viewmodel.VideoChatRegisterViewModel
import com.videochat.presentation.viewmodel.VideoChatRoomViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object VideoChatModule {
    @Provides
    fun provideVideoChatLoginViewModel(
        authenticateUserUseCase: AuthenticateUserUseCase,
        autoAuthenticateUseCase: AutoAuthenticateUseCase
    ): VideoChatLoginViewModel = VideoChatLoginViewModel(authenticateUserUseCase,autoAuthenticateUseCase)

    @Provides
    fun provideVideoChatRegisterViewModel(
        registerUserUseCase: RegisterUserUseCase
    ): VideoChatRegisterViewModel = VideoChatRegisterViewModel(registerUserUseCase)

    @Provides
    fun provideVideoChatRoomViewModel(
    ): VideoChatRoomViewModel = VideoChatRoomViewModel()
}
