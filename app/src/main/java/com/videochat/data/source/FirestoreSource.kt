package com.videochat.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.common.extension.fromBase64String
import com.videochat.common.extension.toDomainModel
import com.videochat.common.extension.toUserEntity
import com.videochat.domain.entity.config.AgoraConfigEntity
import com.videochat.domain.entity.session.SessionEntity
import com.videochat.domain.entity.user.FirestoreUserEntity
import com.videochat.domain.entity.user.UserEntity
import com.videochat.domain.model.config.AgoraConfigModel
import com.videochat.domain.model.session.SessionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Base64
import javax.inject.Inject

class FirestoreSource @Inject constructor(private val fStore: FirebaseFirestore) {

    suspend fun registerUser(userId: String, username: String, email: String, hashedPassword: String, salt: ByteArray, clientUID: Int): Boolean {
        return try {
            val saltBase64 = Base64.getEncoder().encodeToString(salt)
            val userMap = hashMapOf(
                "userId" to userId,
                "userName" to username,
                "userEmail" to email,
                "passwordHash" to hashedPassword,
                "salt" to saltBase64,
                "clientUID" to clientUID
            )
            fStore.collection("users").document(userId).set(userMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getLoginCredentials(userId: String): UserEntity? {
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

    suspend fun getSaltByEmail(email: String): ByteArray? {
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

    suspend fun getUserNameByClientUID(uid: Int): String {
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

    fun getUserCredentials(scope: CoroutineScope,userId: String): LiveData<UserEntity?> {
        val result = MutableLiveData<UserEntity?>()
        scope.launch(Dispatchers.IO) {
            try {
                val docSnapshot = fStore.collection("users").document(userId).get().await()
                if (docSnapshot.exists()) {
                    docSnapshot.toObject(FirestoreUserEntity::class.java)?.let {
                        result.postValue(it.toUserEntity())
                    }
                } else {
                    result.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("FirestoreSource", "Error fetching user credentials", e)
                result.postValue(null)
            }
        }
        return result
    }


    suspend fun getAgoraCredentials(): AgoraConfigModel? {
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

    suspend fun insertSession(session: SessionEntity): Boolean {
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

    suspend fun getSessionsByUserId(uid: String): List<SessionModel> {
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
