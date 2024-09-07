package com.videochat.data.source

import android.util.Log
import com.videochat.domain.entity.SessionEntity
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.model.AgoraConfigDomainModel
import com.videochat.domain.model.SessionDomainModel
import com.videochat.domain.usecase.source.GetAgoraCredentialsUseCase
import com.videochat.domain.usecase.source.GetLoginCredentialsUseCase
import com.videochat.domain.usecase.source.GetSaltByEmailUseCase
import com.videochat.domain.usecase.source.GetSessionsByUserUIDUseCase
import com.videochat.domain.usecase.source.GetUserCredentialsUseCase
import com.videochat.domain.usecase.source.GetUserNameByClientUIDUseCase
import com.videochat.domain.usecase.source.InsertSessionUseCase
import com.videochat.domain.usecase.source.SourceRegisterUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreSource @Inject constructor(
    private val sourceRegisterUserUseCase: SourceRegisterUserUseCase,
    private val getLoginCredentialsUseCase: GetLoginCredentialsUseCase,
    private val getSaltByEmailUseCase: GetSaltByEmailUseCase,
    private val getUserNameByClientUIDUseCase: GetUserNameByClientUIDUseCase,
    private val getUserCredentialsUseCase: GetUserCredentialsUseCase,
    private val getAgoraCredentialsUseCase: GetAgoraCredentialsUseCase,
    private val insertSessionUseCase: InsertSessionUseCase,
    private val getSessionsByUserUIDUseCase: GetSessionsByUserUIDUseCase
) {

    suspend fun registerUser(userId: String, username: String, email: String, hashedPassword: String, salt: ByteArray, clientUID: Int): Boolean {
        return try {
            sourceRegisterUserUseCase.execute(userId, username, email, hashedPassword, salt, clientUID)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getLoginCredentials(userId: String): UserEntity? {
        return try {
            getLoginCredentialsUseCase.execute(userId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSaltByEmail(email: String): ByteArray? {
        return try {
            getSaltByEmailUseCase.execute(email)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserNameByClientUID(uid: Int): String {
        try {
            return getUserNameByClientUIDUseCase.execute(uid)
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error fetching salt by email", e)
        }
        return ""
    }

    fun getUserCredentials(userId: String): Flow<UserEntity?> {
        return getUserCredentialsUseCase.execute(userId)
    }


    suspend fun getAgoraCredentials(): AgoraConfigDomainModel? {
        return try {
            getAgoraCredentialsUseCase.execute()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertSession(session: SessionEntity): Boolean {
        return try {
            insertSessionUseCase.execute(session)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getSessionsByUserId(uid: String): List<SessionDomainModel> {
        return try {
            getSessionsByUserUIDUseCase.execute(uid)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
