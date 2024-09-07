package com.videochat.di

import com.google.firebase.auth.FirebaseAuth
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.repository.AppConfigRepository
import com.videochat.domain.repository.UserCacheRepository
import com.videochat.domain.usecase.app.AuthenticateUserUseCase
import com.videochat.domain.usecase.app.AutoAuthenticateUseCase
import com.videochat.domain.usecase.app.ClearUserCacheUseCase
import com.videochat.domain.usecase.app.GetUserFromCacheUseCase
import com.videochat.domain.usecase.app.LogoutUserUseCase
import com.videochat.domain.usecase.app.RegisterUserUseCase
import com.videochat.domain.usecase.app.SaveUserToCacheUseCase
import com.videochat.domain.usecase.app.SetAppConfigUseCase
import com.videochat.domain.usecase.app.UpdateUserFromCacheUseCase
import com.videochat.domain.usecase.app.UpdateUserInCacheUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppUseCaseModule {
    @Provides
    fun provideAuthenticateUserUseCase(
        fAuth: FirebaseAuth,
        fSource: FirestoreSource
    ): AuthenticateUserUseCase = AuthenticateUserUseCase(fAuth,fSource)

    @Provides
    fun provideAutoAuthenticateUseCase(
        fAuth: FirebaseAuth,
        fSource: FirestoreSource,
        getUserFromCacheUseCase: GetUserFromCacheUseCase
    ): AutoAuthenticateUseCase = AutoAuthenticateUseCase(fAuth,fSource,getUserFromCacheUseCase)

    @Provides
    fun provideClearUserCacheUseCase(
        userCacheRepository: UserCacheRepository
    ): ClearUserCacheUseCase = ClearUserCacheUseCase(userCacheRepository)

    @Provides
    fun provideGetUserFromCacheUseCase(
        userCacheRepository: UserCacheRepository
    ): GetUserFromCacheUseCase = GetUserFromCacheUseCase(userCacheRepository)

    @Provides
    fun provideLogoutUserUseCase(
        fAuth: FirebaseAuth,
        clearUserCacheUseCase: ClearUserCacheUseCase
    ): LogoutUserUseCase = LogoutUserUseCase(fAuth,clearUserCacheUseCase)

    @Provides
    fun provideRegisterUserUseCase(
        fAuth: FirebaseAuth,
        fSource: FirestoreSource
    ): RegisterUserUseCase = RegisterUserUseCase(fAuth,fSource)


    @Provides
    fun provideSaveUserToCacheUseCase(
        userCacheRepository: UserCacheRepository
    ): SaveUserToCacheUseCase = SaveUserToCacheUseCase(userCacheRepository)

    @Provides
    fun provideUpdateUserFromCacheUseCase(
        getUserFromCacheUseCase: GetUserFromCacheUseCase
    ): UpdateUserFromCacheUseCase = UpdateUserFromCacheUseCase(getUserFromCacheUseCase)

    @Provides
    fun provideUpdateUserInCacheUseCase(
        userCacheRepository: UserCacheRepository
    ): UpdateUserInCacheUseCase = UpdateUserInCacheUseCase(userCacheRepository)

    @Provides
    fun provideSetAppConfigUseCase(
        appConfigRepository: AppConfigRepository
    ): SetAppConfigUseCase = SetAppConfigUseCase(appConfigRepository)

    @Provides
    fun provideLoadAppConfigUseCase(
        userCacheRepository: UserCacheRepository
    ): UpdateUserInCacheUseCase = UpdateUserInCacheUseCase(userCacheRepository)

}