package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.common.extension.toUserEntity
import com.videochat.domain.entity.FirestoreUserEntity
import com.videochat.domain.entity.UserEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetLoginCredentialsUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) {
    suspend fun execute(userId: String): UserEntity? {
        return try {
            val docSnapshot = fStore.collection("users").document(userId).get().await()
            if (docSnapshot.exists()) {
                docSnapshot.toObject(FirestoreUserEntity::class.java)?.toUserEntity()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error fetching login credentials", e)
            null
        }
    }
}