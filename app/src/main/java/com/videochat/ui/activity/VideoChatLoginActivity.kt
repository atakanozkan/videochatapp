package com.videochat.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.videochat.base.BaseActivity
import com.videochat.common.navigation.NavigationItem
import com.videochat.data.source.FirestoreSource
import com.videochat.databinding.VideoChatLoginBinding
import com.videochat.domain.entity.user.UserEntity
import com.videochat.domain.model.state.AuthenticationState
import com.videochat.domain.model.state.UiState
import com.videochat.viewModel.AppConfigViewModel
import com.videochat.viewModel.UserViewModel
import com.videochat.viewModel.VideoChatLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class VideoChatLoginActivity : BaseActivity<VideoChatLoginBinding>() {

    private val loginViewModel: VideoChatLoginViewModel by viewModels()
    @Inject
    lateinit var fAuth: FirebaseAuth
    @Inject
    lateinit var fSource: FirestoreSource
    private val userViewModel: UserViewModel by viewModels()
    private val appConfigViewModel: AppConfigViewModel by viewModels()

    override fun initBinding(): VideoChatLoginBinding = VideoChatLoginBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        appConfigViewModel.loadRememberMe()
        setupObservers()
        setupViews()
    }

    private fun setupObservers() {
        loginViewModel.uiState.observe(this, Observer { uiState ->
            render(uiState)
        })

        appConfigViewModel.appConfigModel.observe(this, Observer { config ->
            if (config?.rememberMe == true) {
                checkAutoLogin()
            }
        })
    }

    override fun setupViews() {
        super.setupViews()
        setupLoginButtons()
    }

    private fun checkAutoLogin() {
        render(UiState.Loading)
        loginViewModel.autoAuthenticateFromCache(userViewModel).observe(this) { authState ->
            when (authState) {
                AuthenticationState.Authenticated -> render(UiState.Success)
                AuthenticationState.Unauthenticated -> render(UiState.Error("failed"))
                else -> render(UiState.Loading)
            }
        }
    }


    override fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
                binding.emailTextField.visibility = View.GONE
                binding.passwordTextField.visibility = View.GONE
                binding.btnToRegister.visibility = View.GONE
                binding.cbRememberMe.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.progressBar.visibility = View.GONE
                navigateToAndFinish(NavigationItem.VideChatMain)
            }
            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
                binding.emailTextField.visibility = View.VISIBLE
                binding.passwordTextField.visibility = View.VISIBLE
                binding.btnToRegister.visibility = View.VISIBLE
                binding.cbRememberMe.visibility = View.VISIBLE
                if(uiState.message != ""){
                    showToast(uiState.message)
                }
            }
        }
    }

    private fun setupLoginButtons() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailTextField.editText?.text.toString()
            val password = binding.passwordTextField.editText?.text.toString()

            render(UiState.Loading)

            loginViewModel.authenticationState.observe(this@VideoChatLoginActivity, Observer { authState ->
                when (authState) {
                    AuthenticationState.Authenticated -> {
                        fAuth.currentUser?.uid?.let { userId ->
                            fSource.getUserCredentials(CoroutineScope(SupervisorJob()),userId).observe(this, Observer { userEntity ->
                                if (userEntity != null) {
                                    handleSuccessfulLogin(userEntity, password)
                                } else {
                                    render(UiState.Error("User credentials not found"))
                                }
                            })
                        }
                    }
                    AuthenticationState.Unauthenticated, AuthenticationState.Failed -> render(UiState.Error(""))
                    AuthenticationState.Authenticating -> render(UiState.Loading)
                }
            })
            loginViewModel.authenticateUser(email, password)
        }

        binding.btnToRegister.setOnClickListener {
            navigateToAndFinish(NavigationItem.VideoChatRegister)
        }
    }

    private fun handleSuccessfulLogin(userEntity: UserEntity, password: String) {
        CoroutineScope(SupervisorJob()).launch {
            try {
                if (binding.cbRememberMe.isChecked) {
                    appConfigViewModel.setRememberMe(true)
                } else {
                    appConfigViewModel.setRememberMe(false)
                }

                userViewModel.clearUserFromCache()
                userViewModel.saveUserToCache(
                    userEntity.userId,
                    userEntity.userName,
                    userEntity.userEmail,
                    password,
                    userEntity.salt,
                    userEntity.clientUID
                )

                withContext(Dispatchers.Main) {
                    render(UiState.Success)
                }
            } catch (e: Exception) {
                Log.e("VideoChatLoginActivity", "Error during login", e)
                withContext(Dispatchers.Main) {
                    render(UiState.Error(e.message ?: "Error during login"))
                }
            }
        }
    }

}