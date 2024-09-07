package com.videochat.domain.usecase.app

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val clearUserCacheUseCase: ClearUserCacheUseCase
) {
    suspend fun execute() {
        fAuth.signOut()
        clearUserCacheUseCase.execute()
    }
}
