package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.domain.entity.FirestoreUserEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetUserNameByClientUIDUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) : UseCase {
    suspend fun execute(uid: Int): String {
        try {
            val querySnapshot = fStore.collection("users")
                .whereEqualTo("clientUID", uid)
                .limit(1)
                .get().await()

            if (!querySnapshot.isEmpty) {
                val userDocument = querySnapshot.documents.first()
                val user = userDocument.toObject(FirestoreUserEntity::class.java)
                if (user != null) {
                    return user.userName
                }
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error fetching salt by email", e)
        }
        return ""
    }
}