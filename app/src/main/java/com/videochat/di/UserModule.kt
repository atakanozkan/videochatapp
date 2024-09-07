package com.videochat.di

import com.videochat.domain.usecase.app.ClearUserCacheUseCase
import com.videochat.domain.usecase.app.LogoutUserUseCase
import com.videochat.domain.usecase.app.SaveUserToCacheUseCase
import com.videochat.domain.usecase.app.UpdateUserFromCacheUseCase
import com.videochat.domain.usecase.source.GetSessionsByUserUIDUseCase
import com.videochat.domain.usecase.source.GetUserCredentialsUseCase
import com.videochat.presentation.viewmodel.UserViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    fun provideUserViewModel(
        saveUserToCacheUseCase: SaveUserToCacheUseCase,
        updateUserFromCacheUseCase: UpdateUserFromCacheUseCase,
        clearUserCacheUseCase: ClearUserCacheUseCase,
        logoutUserUseCase: LogoutUserUseCase,
        getUserCredentialsUseCase: GetUserCredentialsUseCase,
        getSessionsByUserUIDUseCase: GetSessionsByUserUIDUseCase
    ): UserViewModel = UserViewModel(saveUserToCacheUseCase,updateUserFromCacheUseCase,clearUserCacheUseCase,logoutUserUseCase,getUserCredentialsUseCase,getSessionsByUserUIDUseCase)
}
