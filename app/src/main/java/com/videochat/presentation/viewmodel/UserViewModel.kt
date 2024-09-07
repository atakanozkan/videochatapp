package com.videochat.presentation.viewmodel

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _userModel = MutableLiveData<UserPresentationModel?>()
    val userModel: MutableLiveData<UserPresentationModel?> get() = _userModel

    private val _sessionHistory = MutableLiveData<List<SessionPresentationModel>>()
    val sessionhistory: LiveData<List<SessionPresentationModel>> get() = _sessionHistory

    private val _userCredentials = MutableStateFlow<UserEntity?>(null)
    val userCredentials: StateFlow<UserEntity?> = _userCredentials.asStateFlow()

    private val _isUserSavedToCache = MutableStateFlow(false)
    val isUserSavedToCache: StateFlow<Boolean> = _isUserSavedToCache.asStateFlow()

    fun saveUserToCache(userId: String, userName: String, email: String, password: String, salt: ByteArray, clientUID: Int) {
        viewModelScope.launch {
            saveUserToCacheUseCase.execute(userId, userName, email, password, salt, clientUID)
        }.invokeOnCompletion {
            _isUserSavedToCache.value = true
        }
    }

    fun loadUserCredentials(userId: String) {
        viewModelScope.launch {
            getUserCredentialsUseCase.execute(userId).collect { userEntity ->
                _userCredentials.value = userEntity
            }
        }
    }

    fun updateFromCache() = viewModelScope.launch {
            val model = updateUserFromCacheUseCase.execute()
            model?.let {
                _userModel.postValue(it)
                updateSessionHistory(model.clientUID)
            }
    }
    private fun updateSessionHistory(uid: Int) {
        viewModelScope.launch {
            val sessions = getSessionsByUserUIDUseCase.execute(uid.toString()).map {
                item->
                    item.toPresentationModel()
            }.toList()
            _sessionHistory.postValue(sessions)
        }
    }
    fun clearUserFromCache() {
        viewModelScope.launch {
            clearUserCacheUseCase.execute()
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            logoutUserUseCase.execute()
            _userModel.postValue(null)
            _userCredentials.value = null
            _isUserSavedToCache.value = false
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
}
