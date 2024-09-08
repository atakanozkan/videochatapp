package com.videochat.domain.usecase.source

import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.architecture.domain.usecase.UseCase
import kotlinx.coroutines.tasks.await
import java.util.Base64
import javax.inject.Inject

class SourceRegisterUserUseCase @Inject constructor(
    private val fStore: FirebaseFirestore
) : UseCase {
    suspend fun execute(userId: String, username: String, email: String, hashedPassword: String, salt: ByteArray, clientUID: Int): Boolean{
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
}