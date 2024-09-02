package com.videochat.domain.model.state

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()

    data class Error(val message: String) : UiState()
}