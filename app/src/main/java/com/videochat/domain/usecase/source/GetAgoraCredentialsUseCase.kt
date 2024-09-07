package com.videochat.domain.usecase.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.common.extension.toDomainModel
import com.videochat.domain.entity.AgoraConfigEntity
import com.videochat.domain.model.AgoraConfigDomainModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetAgoraCredentialsUseCase  @Inject constructor(
    private val fStore: FirebaseFirestore
) {
    suspend fun execute():  AgoraConfigDomainModel? {
        return try {
            val querySnapshot = fStore.collection("config").limit(1).get().await()
            if (!querySnapshot.isEmpty) {
                val docSnapshot = querySnapshot.documents.first()
                docSnapshot.toObject(AgoraConfigEntity::class.java)?.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreSource", "Error fetching agora credentials", e)
            null
        }
    }
}