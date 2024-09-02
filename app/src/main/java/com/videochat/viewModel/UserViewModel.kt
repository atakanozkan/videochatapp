package com.videochat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.videochat.base.BaseViewModel
import com.videochat.common.extension.HashPassword
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.entity.user.UserEntity
import com.videochat.domain.model.session.SessionModel
import com.videochat.domain.model.state.UiState
import com.videochat.domain.model.user.UserModel
import com.videochat.repository.UserCacheRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class UserViewModel @Inject constructor(
    private val userCacheRepository: UserCacheRepository,
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource
) : BaseViewModel<UiState>() {

    private val _userModel = MutableLiveData<UserModel?>()
    val userModel: MutableLiveData<UserModel?> get() = _userModel

    private val _sessionHistory = MutableLiveData<List<SessionModel>>()
    val sessionhistory: LiveData<List<SessionModel>> get() = _sessionHistory

    suspend fun saveUserToCache(userId: String, userName: String, email: String, password: String,salt: ByteArray,clientUID: Int) {
        val hashedPassword = HashPassword.hashPassword(password, salt)
        saveUserLocal(userId, userName, email,clientUID)
        viewModelScope.async {
            userCacheRepository.insertUser(
                UserEntity(
                    userId = userId,
                    userName = userName,
                    userEmail = email,
                    passwordHash = hashedPassword,
                    salt = salt,
                    clientUID = clientUID
            )
            )
        }.await()
    }

    private fun updateSessionHistory(userId: String) {
        viewModelScope.launch {
            val sessions = fSource.getSessionsByUserId(userId)
            _sessionHistory.postValue(sessions)
        }
    }

    fun updateUserInCache(userId: String, userName: String, email: String, password: String,clientUID: Int) {
        val salt = HashPassword.generateSalt()
        val hashedPassword = HashPassword.hashPassword(password, salt)
        viewModelScope.launch {
            userCacheRepository.updateUser(
                UserEntity(
                    userId = userId,
                    userName = userName,
                    userEmail = email,
                    passwordHash = hashedPassword,
                    salt = salt,
                    clientUID = clientUID
            )
            )
        }
    }

    private fun saveUserLocal(userId: String, userName: String, email: String,clientUID: Int){
        val newUserModel = UserModel(userId, email, userName, clientUID)
        _userModel.postValue(newUserModel)
    }

    fun getUserFromCache(): Flow<UserEntity> = flow {
        try {
            val userEntity = userCacheRepository.getUser()
            emit(userEntity)
        } catch (e: Exception) {
            Log.e("user cache get",e.message.toString())
        }
    }.flowOn(Dispatchers.IO)

    fun updateFromCache(){
        viewModelScope.launch {
            val cachedUser = getUserFromCache().firstOrNull()
            if(cachedUser != null){
                val model =
                    UserModel(
                        userId = cachedUser.userId,
                        userName = cachedUser.userName,
                        userEmail = cachedUser.userEmail,
                        clientUID = cachedUser.clientUID
                        )
                userModel.postValue(model)
                updateSessionHistory(cachedUser.clientUID.toString())
            }
        }
    }

    fun clearUserFromCache() {
        viewModelScope.launch {
            userCacheRepository.deleteAll()
        }
    }

    fun getCurrentUserUID(): Int{
        if(_userModel.value == null){
            return 0
        }
        else{
            return _userModel.value!!.clientUID
        }
    }

    fun logoutUser() {
        _userModel.postValue(null)
        fAuth.signOut()
        clearUserFromCache()
        _uiState.postValue(UiState.Success)
    }
}
