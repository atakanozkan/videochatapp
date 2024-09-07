package com.videochat.domain.usecase.app

import com.videochat.presentation.model.UserPresentationModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateUserFromCacheUseCase  @Inject constructor(
    private val getUserFromCacheUseCase: GetUserFromCacheUseCase
){
    suspend fun execute(): UserPresentationModel? {
        val cachedUser = getUserFromCacheUseCase.execute().firstOrNull()
        return cachedUser?.let {
            UserPresentationModel(
                userId = it.userId,
                userName = it.userName,
                userEmail = it.userEmail,
                clientUID = it.clientUID
            )
        }
    }
}
