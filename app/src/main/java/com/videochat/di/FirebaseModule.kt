package com.videochat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.data.source.FirestoreSource
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreSource(
        sourceRegisterUserUseCase: SourceRegisterUserUseCase,
        getLoginCredentialsUseCase: GetLoginCredentialsUseCase,
        getSaltByEmailUseCase: GetSaltByEmailUseCase,
        getUserNameByClientUIDUseCase: GetUserNameByClientUIDUseCase,
        getUserCredentialsUseCase: GetUserCredentialsUseCase,
        getAgoraCredentialsUseCase: GetAgoraCredentialsUseCase,
        insertSessionUseCase: InsertSessionUseCase,
        getSessionsByUserUIDUseCase: GetSessionsByUserUIDUseCase
    ): FirestoreSource = FirestoreSource(
            sourceRegisterUserUseCase,
            getLoginCredentialsUseCase,
            getSaltByEmailUseCase,
            getUserNameByClientUIDUseCase,
            getUserCredentialsUseCase,
            getAgoraCredentialsUseCase,
            insertSessionUseCase,
            getSessionsByUserUIDUseCase)
}
