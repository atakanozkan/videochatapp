package com.videochat.ui.binder

import androidx.core.view.isVisible
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.presentation.model.UiState
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatInCallViewHolder


class VideoChatInCallStateBinder(
    private val viewsProvider: VideoChatInCallViewHolder,
    private val fragmentEvent: FragmentEventListener
)
    : ViewStateBinder<UiState, ViewsProvider> {
    override fun ViewsProvider.bindState(viewState: UiState) {
        viewsProvider.bindState(viewState)
    }

    private fun VideoChatInCallViewHolder.bindState(viewState: UiState) {
        when (viewState) {
            is UiState.Loading -> {
                progressBar.isVisible = true
                scrollView.isVisible = false
                fragmentEvent.onLoadingEvent()
            }
            is UiState.Success -> {
                progressBar.isVisible = false
                scrollView.isVisible = true
                fragmentEvent.onSuccessEvent()
            }
            is UiState.Error -> {
                progressBar.isVisible = false
                fragmentEvent.onErrorEvent()
            }

            UiState.NoChange -> Unit
        }
    }
}
