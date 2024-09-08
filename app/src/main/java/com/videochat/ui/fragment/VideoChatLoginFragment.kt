package com.videochat.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.videochat.R
import com.videochat.architecture.ui.view.BaseFragment
import com.videochat.data.source.FirestoreSource
import com.videochat.databinding.VideoChatLoginFragmentBinding
import com.videochat.domain.entity.UserEntity
import com.videochat.presentation.model.AuthenticationState
import com.videochat.presentation.model.UiState
import com.videochat.presentation.viewmodel.AppConfigViewModel
import com.videochat.presentation.viewmodel.UserViewModel
import com.videochat.presentation.viewmodel.VideoChatLoginViewModel
import com.videochat.ui.binder.VideoChatLoginStateBinder
import com.videochat.ui.destination.RouteDestination
import com.videochat.ui.event.FragmentEventListener
import com.videochat.ui.holder.VideoChatLoginViewHolder
import com.videochat.ui.navigation.RouteDestinationToUiMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class VideoChatLoginFragment : BaseFragment<UiState,VideoChatLoginFragmentBinding>(
    layoutResourceId = R.layout.video_chat_login_fragment,
) {
    @Inject
    lateinit var userViewModel: UserViewModel
    @Inject
    lateinit var  appConfigViewModel: AppConfigViewModel
    @Inject
    override lateinit var viewModel: VideoChatLoginViewModel

    @Inject
    lateinit var fAuth: FirebaseAuth
    @Inject
    lateinit var fSource: FirestoreSource

    @Inject
    override lateinit var destinationToUiMapper: RouteDestinationToUiMapper

    private lateinit var viewHolder: VideoChatLoginViewHolder

    private var fragmentEventListener: FragmentEventListener = object : FragmentEventListener {
        override fun onSuccessEvent() {
            Log.d("FragmentEventListener", "Success event triggered")
            navigate(RouteDestination.Home)
        }

        override fun onLoadingEvent() {
            Log.d("FragmentEventListener", "Loading event triggered")
        }

        override fun onErrorEvent() {
            Log.d("FragmentEventListener", "Error event triggered")
        }
    }

    override lateinit var viewStateBinder: VideoChatLoginStateBinder

    override fun View.bindViews() {
        setupViews()
    }

    override fun setupViews() {
        observeViewModel()
        setupLoginButtons()
        appConfigViewModel.loadRememberMe()
    }

    override fun initializeBinding(inflater: LayoutInflater, container: ViewGroup?): VideoChatLoginFragmentBinding {
        val binding = VideoChatLoginFragmentBinding.inflate(inflater, container, false)
        viewHolder = VideoChatLoginViewHolder(binding.root)
        viewStateBinder = VideoChatLoginStateBinder(viewHolder,fragmentEventListener)
        return binding
    }


    private fun observeViewModel() {
        appConfigViewModel.appConfigModel.observe(viewLifecycleOwner, Observer { config ->
            if (config?.rememberMe == true) {
                checkAutoLogin()
            }
        })
    }

    private fun checkAutoLogin() {
        render(UiState.Loading)
        viewModel.autoAuthenticate().observe(viewLifecycleOwner) { authState ->
            when (authState) {
                AuthenticationState.Authenticated -> render(UiState.Success)
                AuthenticationState.Unauthenticated -> render(UiState.Error("failed"))
                else -> render(UiState.Loading)
            }
        }
    }

    private fun setupLoginButtons() {
        binding.btnLoginLogin.setOnClickListener {
            val email = binding.emailTextFieldLogin.editText?.text.toString()
            val password = binding.passwordTextFieldLogin.editText?.text.toString()
            viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
                when (authState) {
                    AuthenticationState.Authenticated -> {
                        fAuth.currentUser?.uid?.let { userId ->
                            userViewModel.loadUserCredentials(userId)
                        }
                    }
                    AuthenticationState.Unauthenticated, AuthenticationState.Failed -> render(UiState.Error(""))
                    AuthenticationState.Authenticating -> render(UiState.Loading)
                    null -> render(UiState.Error(""))
                }
            })
            viewModel.authenticateUser(email, password)

            lifecycleScope.launch {
                userViewModel.userCredentials.collect{
                        credential->
                    if (credential != null) {
                        handleSuccessfulLogin(credential, password)
                    } else {
                        render(UiState.Error("User credentials not found"))
                    }
                }
            }

        }

        binding.btnToRegisterLogin.setOnClickListener {
            navigate(RouteDestination.Register)
        }
    }

    private fun handleSuccessfulLogin(userEntity: UserEntity, password: String) {
        lifecycleScope.launch {
            try {
                if (binding.cbRememberMeLogin.isChecked) {
                    appConfigViewModel.setRememberMe(true)
                } else {
                    appConfigViewModel.setRememberMe(false)
                }

                val clearJob = launch {
                        userViewModel.clearUserFromCache()
                }

                clearJob.join()
                val saveJob = launch {
                    userViewModel.saveUserToCache(
                        userEntity.userId,
                        userEntity.userName,
                        userEntity.userEmail,
                        password,
                        userEntity.salt,
                        userEntity.clientUID
                    )
                }
                saveJob.join()
                repeatOnLifecycle(Lifecycle.State.RESUMED){
                    userViewModel.isUserSavedToCache.collect { isSaved ->
                        if (isSaved) {
                            render(UiState.Success)
                        } else {
                            render(UiState.Error("Failed to save user to cache"))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("VideoChatLoginFragment", "Error during login", e)
                withContext(Dispatchers.Main) {
                    render(UiState.Error(e.message ?: "Error during login"))
                }
            }
        }
    }
    private fun render(uiState: UiState) {
        applyViewState(uiState)
    }
}
