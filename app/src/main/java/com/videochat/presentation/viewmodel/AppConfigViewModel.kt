package com.videochat.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videochat.architecture.presentation.viewmodel.BaseViewModel
import com.videochat.domain.model.AgoraConfigDomainModel
import com.videochat.domain.usecase.app.LoadAppConfigUseCase
import com.videochat.domain.usecase.app.SetAppConfigUseCase
import com.videochat.domain.usecase.source.GetAgoraCredentialsUseCase
import com.videochat.presentation.model.AppConfigPresentationModel
import com.videochat.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppConfigViewModel @Inject constructor(
    private val setAppConfigUseCase: SetAppConfigUseCase,
    private val loadAppConfigUseCase: LoadAppConfigUseCase,
    private val getAgoraCredentialsUseCase: GetAgoraCredentialsUseCase,
) : BaseViewModel<UiState>(UiState.NoChange) {

    private val _appConfigModel = MutableLiveData<AppConfigPresentationModel?>()
    val appConfigModel: LiveData<AppConfigPresentationModel?> = _appConfigModel

    private val _agoraCredentials = MutableStateFlow<AgoraConfigDomainModel?>(null)
    val agoraCredentials: StateFlow<AgoraConfigDomainModel?> = _agoraCredentials.asStateFlow()

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

    fun getAgoraCredentials() {
        viewModelScope.launch {
            try {
                val credentials = getAgoraCredentialsUseCase.execute()
                _agoraCredentials.emit(credentials)
            } catch (e: Exception) {
                _agoraCredentials.emit(null)
            }
        }
    }
}
