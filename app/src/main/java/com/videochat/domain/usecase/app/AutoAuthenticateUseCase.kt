package com.videochat.domain.usecase.app

import com.google.firebase.auth.FirebaseAuth
import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.model.AuthenticationState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AutoAuthenticateUseCase @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource,
    private val getUserFromCacheUseCase: GetUserFromCacheUseCase
) : UseCase {

    suspend fun execute(): AuthenticationState {
        return try {
            val cachedUser = getUserFromCacheUseCase.execute().firstOrNull() ?: return AuthenticationState.Unauthenticated

            val firestoreUser = fSource.getLoginCredentials(cachedUser.userId)
            if (firestoreUser == null || cachedUser.passwordHash != firestoreUser.passwordHash) {
                return AuthenticationState.Unauthenticated
            }

            val result = fAuth.signInWithEmailAndPassword(firestoreUser.userEmail, cachedUser.passwordHash).await()
            if (result.user != null) {
                AuthenticationState.Authenticated
            } else {
                AuthenticationState.Unauthenticated
            }
        } catch (exception: Exception) {
            AuthenticationState.Unauthenticated
        }
    }
}
