package com.videochat.repository

import com.videochat.data.dao.UserDao
import com.videochat.domain.entity.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserCacheRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun getUser(): UserEntity {
        return withContext(Dispatchers.IO) {
            userDao.getUser()
        }
    }

    suspend fun insertUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    suspend fun updateUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            userDao.deleteUser()
        }
    }
}
