package com.videochat.ui.holder

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.videochat.R
import com.videochat.ui.provider.VideoChatRegisterViewsProvider

class VideoChatRegisterViewHolder(view: View
) : VideoChatRegisterViewsProvider {
    override val progressBar: ProgressBar = view.findViewById(R.id.progressBar_register)
    override val btnRegister: Button = view.findViewById(R.id.btnRegister_register)
    override val userNameTextField: TextInputLayout = view.findViewById(R.id.userNameTextField_register)
    override val emailTextField: TextInputLayout = view.findViewById(R.id.emailTextField_register)
    override val passwordTextField: TextInputLayout = view.findViewById(R.id.passwordTextField_register)
    override val btnBackLogin: TextView = view.findViewById(R.id.btnBackLogin_register)
}