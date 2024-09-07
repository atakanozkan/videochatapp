package com.videochat.ui.binder

import android.view.View
import androidx.core.view.isVisible
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.presentation.model.UiState
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatStartCallViewHolder


class VideoChatStartCallStateBinder(
    private val viewsProvider: VideoChatStartCallViewHolder,
    private val fragmentEvent: FragmentEventListener
)
    : ViewStateBinder<UiState, ViewsProvider> {
    override fun ViewsProvider.bindState(viewState: UiState) {
        viewsProvider.bindState(viewState)
    }

    private fun VideoChatStartCallViewHolder.bindState(viewState: UiState) {
        when (viewState) {
            is UiState.Loading -> {
                progressBar.isVisible = true
                fragmentEvent.onLoadingEvent()
            }
            is UiState.Success -> {
                progressBar.visibility = View.GONE
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
