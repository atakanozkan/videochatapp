package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.common.extension.toDomainModel
import com.videochat.domain.entity.SessionEntity
import com.videochat.domain.model.SessionDomainModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetSessionsByUserUIDUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) {
    suspend fun execute(uid: String): List<SessionDomainModel> {
        return try {
            val callerQuerySnapshot = fStore.collection("sessions")
                .whereEqualTo("callerId", uid)
                .get().await()
            val receiverQuerySnapshot = fStore.collection("sessions")
                .whereEqualTo("receiverId", uid)
                .get().await()
            val allSessions = (callerQuerySnapshot.documents + receiverQuerySnapshot.documents)
                .mapNotNull { it.toObject(SessionEntity::class.java)?.toDomainModel() }
                .distinctBy { it.sessionId }

            allSessions
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error fetching sessions for user ID", e)
            emptyList()
        }
    }
}