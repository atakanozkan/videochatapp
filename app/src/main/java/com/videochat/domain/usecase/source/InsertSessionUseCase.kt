package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.domain.entity.SessionEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InsertSessionUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) : UseCase {
    suspend fun execute(session: SessionEntity): Boolean {
        return try {
            fStore.collection("sessions")
                .document(session.sessionId)
                .set(session)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error inserting session", e)
            false
        }
    }
}