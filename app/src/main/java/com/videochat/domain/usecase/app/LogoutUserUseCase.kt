package com.videochat.domain.usecase.app

import com.google.firebase.auth.FirebaseAuth
import com.videochat.architecture.domain.usecase.UseCase
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val clearUserCacheUseCase: ClearUserCacheUseCase
): UseCase {
    suspend fun execute() {
        fAuth.signOut()
        clearUserCacheUseCase.execute()
    }
}
