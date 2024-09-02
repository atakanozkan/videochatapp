package com.videochat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videochat.base.BaseViewModel
import com.videochat.domain.entity.config.AppConfigEntity
import com.videochat.domain.model.config.AppConfigModel
import com.videochat.domain.model.state.UiState
import com.videochat.repository.AppConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppConfigViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : BaseViewModel<UiState>() {

    private val _appConfigModel = MutableLiveData<AppConfigModel?>()
    val appConfigModel: LiveData<AppConfigModel?> = _appConfigModel

    fun setRememberMe(rememberMe: Boolean) {
        viewModelScope.launch {
            appConfigRepository.setRememberMe(rememberMe)
            _appConfigModel.postValue(AppConfigModel(rememberMe = rememberMe))
        }
    }

    fun loadRememberMe() {
        viewModelScope.launch {
            var appConfig = appConfigRepository.getConfig()
            if (appConfig == null) {
                appConfig = AppConfigEntity(rememberMe = false)
                appConfigRepository.insertAppConfig(appConfig)
            }
            _appConfigModel.postValue(AppConfigModel(rememberMe = appConfig.rememberMe))
        }
    }
}
