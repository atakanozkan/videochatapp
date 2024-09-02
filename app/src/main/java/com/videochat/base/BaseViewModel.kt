package com.videochat.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel<T> : ViewModel() {
    val _uiState: MutableLiveData<T> = MutableLiveData()
    val uiState: LiveData<T> get() = _uiState
}
