package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.common.extension.toUserEntity
import com.videochat.domain.entity.FirestoreUserEntity
import com.videochat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetUserCredentialsUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) : UseCase {
    fun execute(userId: String): Flow<UserEntity?> = flow {
        try {
            val docSnapshot = fStore.collection("users").document(userId).get().await()
            if (docSnapshot.exists()) {
                val userEntity = docSnapshot.toObject(FirestoreUserEntity::class.java)?.toUserEntity()
                emit(userEntity)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error fetching user credentials", e)
            emit(null)
        }
    }
}