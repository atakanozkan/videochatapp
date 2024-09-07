package com.videochat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.common.extension.toPresentationModel
import com.videochat.domain.usecase.app.AuthenticateUserUseCase
import com.videochat.domain.usecase.app.AutoAuthenticateUseCase
import com.videochat.presentation.model.AuthenticationState
import com.videochat.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoChatLoginViewModel @Inject constructor(
    private val authenticateUserUseCase: AuthenticateUserUseCase,
    private val autoAuthenticateUseCase: AutoAuthenticateUseCase
): BaseViewModel<UiState>(UiState.NoChange) {
    val authenticationState = MutableLiveData<AuthenticationState>()

    fun authenticateUser(email: String, password: String): LiveData<AuthenticationState> {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationState.postValue(AuthenticationState.Authenticating)
            val state = authenticateUserUseCase.execute(email, password)
            Log.d("authenticeting",state.name)
            authenticationState.postValue(state.toPresentationModel())
        }
        return authenticationState
    }


    fun autoAuthenticate(): LiveData<AuthenticationState> {
        viewModelScope.launch(Dispatchers.IO) {
            val state = autoAuthenticateUseCase.execute()
            authenticationState.postValue(state.toPresentationModel())
        }
        return authenticationState
    }
}