package com.videochat.domain.usecase.app

import com.videochat.architecture.domain.usecase.UseCase
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.repository.UserCacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserFromCacheUseCase @Inject constructor(
    private val userCacheRepository: UserCacheRepository
): UseCase {
    fun execute(): Flow<UserEntity> = flow {
        val userEntity = userCacheRepository.getUser()
        emit(userEntity)
    }.flowOn(Dispatchers.IO)
}
