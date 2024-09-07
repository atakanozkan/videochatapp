package com.videochat.ui.provider

import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.videochat.architecture.ui.binder.ViewsProvider

interface VideoChatRegisterViewsProvider : ViewsProvider {
    val progressBar: ProgressBar
    val btnRegister: Button
    val userNameTextField: TextInputLayout
    val emailTextField: TextInputLayout
    val passwordTextField: TextInputLayout
    val btnBackLogin: TextView
}