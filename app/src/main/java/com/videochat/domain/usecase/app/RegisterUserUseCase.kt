package com.videochat.domain.usecase.app

import com.google.firebase.auth.FirebaseAuth
import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.common.extension.HashPassword
import com.videochat.common.extension.UniqueIdGenerator.generateUniqueId
import com.videochat.data.source.FirestoreSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource
): UseCase {
    suspend fun execute(email: String, username: String, password: String): Result<String> {
        return try {
            if (!validateUsername(username) || !validatePassword(password)) {
                throw IllegalArgumentException("Validation failed")
            }

            val generatedSalt = HashPassword.generateSalt()
            val hashedPassword = HashPassword.hashPassword(password, generatedSalt)
            fAuth.createUserWithEmailAndPassword(email, hashedPassword).await()
            val uniqueClientUID = generateUniqueId()
            val userId = fAuth.currentUser?.uid ?: throw Exception("Failed to create user account.")

            val registrationSuccess = fSource.registerUser(
                userId, username, email, hashedPassword, generatedSalt, uniqueClientUID
            )

            if (registrationSuccess) {
                Result.success(userId)
            } else {
                Result.failure(Exception("Failed to save user data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateUsername(username: String): Boolean =
        username.length >= 6 && username.any { it.isDigit() } && username.any { it.isLetter() }

    private fun validatePassword(password: String): Boolean =
        password.length >= 6
}
