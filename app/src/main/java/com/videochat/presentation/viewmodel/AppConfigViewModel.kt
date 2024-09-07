package com.videochat.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.domain.usecase.app.LoadAppConfigUseCase
import com.videochat.domain.usecase.app.SetAppConfigUseCase
import com.videochat.presentation.model.AppConfigPresentationModel
import com.videochat.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppConfigViewModel @Inject constructor(
    private val setAppConfigUseCase: SetAppConfigUseCase,
    private val loadAppConfigUseCase: LoadAppConfigUseCase
) : BaseViewModel<UiState>(UiState.NoChange) {

    private val _appConfigModel = MutableLiveData<AppConfigPresentationModel?>()
    val appConfigModel: LiveData<AppConfigPresentationModel?> = _appConfigModel


    fun setRememberMe(rememberMe: Boolean) {
        viewModelScope.launch {
            try {
                setAppConfigUseCase.execute(rememberMe)
                _appConfigModel.postValue(AppConfigPresentationModel(rememberMe = rememberMe))
            } catch (e: Exception) {
                updateViewState(UiState.Error(e.message ?: "Failed to update config"))
            }
        }
    }

    fun loadRememberMe() {
        viewModelScope.launch {
            try {
                val appConfig = loadAppConfigUseCase.execute()
                _appConfigModel.postValue(AppConfigPresentationModel(rememberMe = appConfig.rememberMe))
            } catch (e: Exception) {
                updateViewState(UiState.Error(e.message ?: "Failed to load config"))
            }
        }
    }
}
