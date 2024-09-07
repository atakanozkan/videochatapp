package com.videochat.ui.binder

import android.util.Log
import android.view.View
import com.videochat.architecture.ui.binder.ViewStateBinder
import com.videochat.architecture.ui.binder.ViewsProvider
import com.videochat.presentation.model.UiState
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatLoginViewHolder


class VideoChatLoginStateBinder(
    private val viewsProvider: VideoChatLoginViewHolder,
    private val fragmentEvent: FragmentEventListener
    )
    : ViewStateBinder<UiState, ViewsProvider> {
    override fun ViewsProvider.bindState(viewState: UiState) {
        viewsProvider.bindState(viewState)
    }

    private fun VideoChatLoginViewHolder.bindState(viewState: UiState) {
        when (viewState) {
            is UiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                Log.d("progress bar",progressBar.toString())
                Log.d("progress bar",progressBar.parent.toString())
                btnLogin.visibility = View.GONE
                emailTextField.visibility = View.GONE
                passwordTextField.visibility = View.GONE
                btnToRegister.visibility = View.GONE
                cbRememberMe.visibility = View.GONE
                fragmentEvent.onLoadingEvent()
            }
            is UiState.Success -> {
                progressBar.visibility = View.GONE
                fragmentEvent.onSuccessEvent()
            }
            is UiState.Error -> {
                progressBar.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
                emailTextField.visibility = View.VISIBLE
                passwordTextField.visibility = View.VISIBLE
                btnToRegister.visibility = View.VISIBLE
                cbRememberMe.visibility = View.VISIBLE
                fragmentEvent.onErrorEvent()
            }
            UiState.NoChange -> Unit
        }
    }

}
