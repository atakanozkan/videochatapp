package com.videochat.ui.holder

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.videochat.R
import com.videochat.ui.provider.VideoChatLoginViewsProvider

class VideoChatLoginViewHolder(view: View) : VideoChatLoginViewsProvider {
    override val progressBar: ProgressBar = view.findViewById(R.id.progressBar_login)
    override val btnLogin: Button = view.findViewById(R.id.btnLogin_login)
    override val emailTextField: TextInputLayout = view.findViewById(R.id.emailTextField_login)
    override val passwordTextField: TextInputLayout = view.findViewById(R.id.passwordTextField_login)
    override val btnToRegister: TextView = view.findViewById(R.id.btnToRegister_login)
    override val cbRememberMe: CheckBox = view.findViewById(R.id.cbRememberMe_login)
}
