package com.videochat.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.domain.usecase.app.RegisterUserUseCase
import com.videochat.presentation.model.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoChatRegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
) : BaseViewModel<UiState>(UiState.NoChange) {

    fun registerUser(email: String, username: String, password: String) {
        updateViewState(UiState.Loading)
        viewModelScope.launch {
            try {
                val result = registerUserUseCase.execute(email, username, password)
                if (result.isSuccess) {
                    updateViewState(UiState.Success)
                } else {
                    updateViewState(UiState.Error(result.exceptionOrNull()?.message ?: "Registration failed"))
                }
            } catch (e: Exception) {
                updateViewState(UiState.Error(e.message ?: "Registration failed"))
            }
        }
    }
}