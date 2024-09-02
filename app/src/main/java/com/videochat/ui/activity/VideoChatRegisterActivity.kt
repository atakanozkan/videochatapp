package com.videochat.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.videochat.base.BaseActivity
import com.videochat.common.navigation.NavigationItem
import com.videochat.databinding.VideoChatRegisterBinding
import com.videochat.domain.model.state.UiState
import com.videochat.viewModel.VideoChatRegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoChatRegisterActivity : BaseActivity<VideoChatRegisterBinding>() {

    private val registerViewModel: VideoChatRegisterViewModel by viewModels()

    override fun initBinding(): VideoChatRegisterBinding = VideoChatRegisterBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeViewModel()
        setupViews()
    }

    override fun setupViews() {
        super.setupViews()
        setupRegisterButtons()

    }

    private fun observeViewModel() {
        registerViewModel.uiState.observe(this) { uiState ->
            render(uiState)
        }
    }

    override fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading ->{
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                navigateToAndFinish(NavigationItem.VideoChatLogin)
                showToast("Account is created successfully!")
            }
            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.visibility = View.VISIBLE
                showToast(
                    (uiState as UiState.Error).message )
            }
        }
    }

    private fun setupRegisterButtons() {
        binding.btnRegister.setOnClickListener {
            val email = binding.emailTextField.editText?.text.toString()
            val username = binding.userNameTextField.editText?.text.toString()
            val password = binding.passwordTextField.editText?.text.toString()
            registerViewModel.registerUser(email, username, password)
        }
        binding.btnBackLogin.setOnClickListener {
            navigateToAndFinish(NavigationItem.VideoChatLogin)
        }
    }
}