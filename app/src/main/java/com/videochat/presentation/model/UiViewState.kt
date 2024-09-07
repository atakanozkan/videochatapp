package com.videochat.presentation.model

sealed interface UiViewState {
    data object NoChange : UiViewState

    data object Loading : UiViewState

    data object Success : UiViewState

    data object Error : UiViewState
}