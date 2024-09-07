package com.videochat.presentation.model

sealed class UiState {
    object NoChange : UiState()
    object Loading : UiState()
    object Success : UiState()

    data class Error(val message: String) : UiState()
}