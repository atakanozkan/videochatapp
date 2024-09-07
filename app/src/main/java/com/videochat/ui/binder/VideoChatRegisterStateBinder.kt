package com.videochat.ui.binder

import android.view.View
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.presentation.model.UiState
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.provider.VideoChatRegisterViewsProvider


class VideoChatRegisterStateBinder(
    private val viewsProvider: VideoChatRegisterViewsProvider,
    private val fragmentEvent: FragmentEventListener
): ViewStateBinder<UiState, ViewsProvider> {
    override fun ViewsProvider.bindState(viewState: UiState) {
        viewsProvider.bindState(viewState)
    }

    private fun VideoChatRegisterViewsProvider.bindState(viewState: UiState) {
        when (viewState) {
            is UiState.Loading ->{
                progressBar.visibility = View.VISIBLE
                btnRegister.visibility = View.GONE
                fragmentEvent.onLoadingEvent()
            }
            is UiState.Success -> {
                progressBar.visibility = View.GONE
                fragmentEvent.onSuccessEvent()
            }
            is UiState.Error -> {
                progressBar.visibility = View.GONE
                btnRegister.visibility = View.VISIBLE
                fragmentEvent.onErrorEvent()
            }
            UiState.NoChange -> Unit
        }
    }
}
