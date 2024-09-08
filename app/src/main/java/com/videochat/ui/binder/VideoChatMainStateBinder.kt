package com.videochat.ui.binder

import android.view.View
import androidx.core.view.isVisible
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.presentation.model.UiState
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatMainViewHolder


class VideoChatMainStateBinder(
    private val viewsProvider: VideoChatMainViewHolder,
    private val fragmentEvent: FragmentEventListener
)
    : ViewStateBinder<UiState, ViewsProvider> {
    override fun ViewsProvider.bindState(viewState: UiState) {
        viewsProvider.bindState(viewState)
    }

    private fun VideoChatMainViewHolder.bindState(viewState: UiState) {
        when (viewState) {
            is UiState.Loading -> {
                progressBar.isVisible = true
                tvUserRetrieveMessage.isVisible = true
                tvUsername.isVisible = false
                btnLogout.isVisible = false
                btnAddCall.isVisible = false
                tvNoCalls.isVisible = false
                fragmentEvent.onLoadingEvent()
            }
            is UiState.Success -> {
                progressBar.visibility = View.GONE
                tvUserRetrieveMessage.isVisible = false
                tvUsername.isVisible = true
                btnLogout.isVisible = true
                btnAddCall.isVisible = true
                fragmentEvent.onSuccessEvent()
            }
            is UiState.Error -> {
                progressBar.isVisible = false
                tvUserRetrieveMessage.isVisible = false
                tvNoCalls.isVisible = true
                fragmentEvent.onErrorEvent()
            }
            UiState.NoChange -> Unit
        }
    }

}
