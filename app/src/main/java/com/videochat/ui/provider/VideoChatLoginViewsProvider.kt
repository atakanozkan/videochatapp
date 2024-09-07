package com.videochat.ui.provider

import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.videochat.architecture.ui.binder.ViewsProvider

interface VideoChatLoginViewsProvider : ViewsProvider {
    val progressBar: ProgressBar
    val btnLogin: Button
    val emailTextField: TextInputLayout
    val passwordTextField: TextInputLayout
    val btnToRegister: TextView
    val cbRememberMe: CheckBox
}