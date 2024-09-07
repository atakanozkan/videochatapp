package com.videochat.di

import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.domain.usecase.source.GetAgoraCredentialsUseCase
import com.videochat.domain.usecase.source.GetLoginCredentialsUseCase
import com.videochat.domain.usecase.source.GetSaltByEmailUseCase
import com.videochat.domain.usecase.source.GetSessionsByUserUIDUseCase
import com.videochat.domain.usecase.source.GetUserCredentialsUseCase
import com.videochat.domain.usecase.source.GetUserNameByClientUIDUseCase
import com.videochat.domain.usecase.source.InsertSessionUseCase
import com.videochat.domain.usecase.source.SourceRegisterUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SourceUseCaseModule {
    @Provides
    fun provideGetAgoraCredentialsUseCase(
        fStore: FirebaseFirestore,
    ): GetAgoraCredentialsUseCase = GetAgoraCredentialsUseCase(fStore)

    @Provides
    fun provideGetLoginCredentialsUseCase(
        fStore: FirebaseFirestore,
    ): GetLoginCredentialsUseCase = GetLoginCredentialsUseCase(fStore)

    @Provides
    fun provideGetSaltByEmailUseCase(
        fStore: FirebaseFirestore,
    ): GetSaltByEmailUseCase = GetSaltByEmailUseCase(fStore)
    @Provides
    fun provideGetSessionsByUserUIDUseCase(
        fStore: FirebaseFirestore,
    ): GetSessionsByUserUIDUseCase = GetSessionsByUserUIDUseCase(fStore)

    @Provides
    fun provideGetUserCredentialsUseCase(
        fStore: FirebaseFirestore,
    ): GetUserCredentialsUseCase = GetUserCredentialsUseCase(fStore)

    @Provides
    fun provideGetUserNameByClientUIDUseCase(
        fStore: FirebaseFirestore,
    ): GetUserNameByClientUIDUseCase = GetUserNameByClientUIDUseCase(fStore)

    @Provides
    fun provideInsertSessionUseCase(
        fStore: FirebaseFirestore,
    ): InsertSessionUseCase = InsertSessionUseCase(fStore)

    @Provides
    fun provideSourceRegisterUserUseCase(
        fStore: FirebaseFirestore,
    ): SourceRegisterUserUseCase = SourceRegisterUserUseCase(fStore)

}