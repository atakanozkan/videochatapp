package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.common.extension.fromBase64String
import com.videochat.domain.entity.FirestoreUserEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetSaltByEmailUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) {

    suspend fun execute(email: String): ByteArray? {
        try {
            val querySnapshot = fStore.collection("users")
                .whereEqualTo("userEmail", email)
                .limit(1)
                .get().await()

            if (!querySnapshot.isEmpty) {
                val userDocument = querySnapshot.documents.first()
                val user = userDocument.toObject(FirestoreUserEntity::class.java)
                return user?.salt?.fromBase64String()
            }
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error fetching salt by email", e)
        }
        return null
    }
}