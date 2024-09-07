package com.videochat.domain.usecase.app

import com.google.firebase.auth.FirebaseAuth
import com.videochat.common.extension.HashPassword
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.model.AuthenticationState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticateUserUseCase @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource
){
    suspend fun execute(email: String, password: String): AuthenticationState {
        return try {
            val salt = fSource.getSaltByEmail(email)
            salt?.let {
                val hashedPassword = HashPassword.hashPassword(password, salt)
                val result = fAuth.signInWithEmailAndPassword(email, hashedPassword).await()
                if (result.user != null) {
                    AuthenticationState.Authenticated
                } else {
                    AuthenticationState.Unauthenticated
                }
            } ?: AuthenticationState.Unauthenticated
        } catch (e: Exception) {
            AuthenticationState.Unauthenticated
        }
    }
}
