package com.videochat.architecture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.videochat.architecture.presentation.destination.BaseDestination
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<VIEW_STATE>(
    initialViewState: VIEW_STATE
) : ViewModel() {

    val uiState =  MutableStateFlow(initialViewState)
    val _uiState = uiState.asStateFlow()

    val destination = MutableSharedFlow<BaseDestination>()
    val _destination = destination.asSharedFlow()

    protected fun updateViewState(newState: VIEW_STATE) {
        MainScope().launch {
            uiState.emit(newState)
        }
    }

    protected fun navigate(destination: BaseDestination) {
        MainScope().launch {
            this@BaseViewModel.destination.emit(destination)
        }
    }

    protected fun navigateBack() {
        MainScope().launch {
            destination.emit(BaseDestination.Back)
        }
    }
}
