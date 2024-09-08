package com.videochat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.common.extension.toPresentationModel
import com.videochat.domain.entity.UserEntity
import com.videochat.domain.usecase.app.ClearUserCacheUseCase
import com.videochat.domain.usecase.app.LogoutUserUseCase
import com.videochat.domain.usecase.app.SaveUserToCacheUseCase
import com.videochat.domain.usecase.app.UpdateUserFromCacheUseCase
import com.videochat.domain.usecase.source.GetSessionsByUserUIDUseCase
import com.videochat.domain.usecase.source.GetUserCredentialsUseCase
import com.videochat.presentation.model.SessionPresentationModel
import com.videochat.presentation.model.UiState
import com.videochat.presentation.model.UserPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class UserViewModel @Inject constructor(
    private val saveUserToCacheUseCase: SaveUserToCacheUseCase,
    private val updateUserFromCacheUseCase: UpdateUserFromCacheUseCase,
    private val clearUserCacheUseCase: ClearUserCacheUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val getUserCredentialsUseCase: GetUserCredentialsUseCase,
    private val getSessionsByUserUIDUseCase: GetSessionsByUserUIDUseCase
) : BaseViewModel<UiState>(UiState.NoChange) {

    private val _userModel = MutableStateFlow<UserPresentationModel?>(null)
    val userModel: StateFlow<UserPresentationModel?> = _userModel.asStateFlow()

    private val _sessionHistory = MutableLiveData<List<SessionPresentationModel>>()
    val sessionhistory: LiveData<List<SessionPresentationModel>> get() = _sessionHistory

    private val _userCredentials = MutableStateFlow<UserEntity?>(null)
    val userCredentials: StateFlow<UserEntity?> = _userCredentials.asStateFlow()

    private val _isUserSavedToCache = MutableStateFlow(false)
    val isUserSavedToCache: StateFlow<Boolean> = _isUserSavedToCache.asStateFlow()

    fun saveUserToCache(userId: String, userName: String, email: String, password: String, salt: ByteArray, clientUID: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            saveUserToCacheUseCase.execute(userId, userName, email, password, salt, clientUID)
            _isUserSavedToCache.value = true
        } catch (e: Exception) {
            Log.e("saveUserToCache", "Failed to save user to cache", e)
            _isUserSavedToCache.value = false
        }
    }

    fun loadUserCredentials(userId: String) = viewModelScope.launch {
        getUserCredentialsUseCase.execute(userId).collect { userEntity ->
            _userCredentials.value = userEntity
        }
    }

    fun updateFromCache() = viewModelScope.launch {
            val model = updateUserFromCacheUseCase.execute()
            model?.let {
                withContext(Dispatchers.Main){
                    _userModel.value= it
                }

                updateSessionHistory(model.clientUID)
            }
    }
    private fun updateSessionHistory(uid: Int) {
        viewModelScope.launch {
            val sessions = getSessionsByUserUIDUseCase.execute(uid.toString()).map {
                item->
                    item.toPresentationModel()
            }.toList()
            withContext(Dispatchers.Main){
                _sessionHistory.value= sessions
            }
        }
    }
    fun clearUserFromCache() = viewModelScope.launch(Dispatchers.IO){
        clearUserCacheUseCase.execute()
    }

    fun logoutUser() {
        viewModelScope.launch {
            logoutUserUseCase.execute()
            _userModel.value = null
            _userCredentials.value = null
            _isUserSavedToCache.value = false
        }
    }

    fun createUserModel(userId: String,userEmail: String,userName: String,clientUID: Int): UserPresentationModel{
        val model = UserPresentationModel(userId,userEmail,userName,clientUID)
        _userModel.value = model
        updateSessionHistory(model.clientUID)
        return model
    }

    fun getCurrentUserUID(): Int{
        return if(_userModel.value == null){
            0
        } else{
            _userModel.value!!.clientUID
        }
    }
}
