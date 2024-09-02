package com.videochat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.videochat.base.BaseViewModel
import com.videochat.common.extension.HashPassword
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.model.state.AuthenticationState
import com.videochat.domain.model.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class VideoChatLoginViewModel @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource
): BaseViewModel<UiState>() {
    val authenticationState = MutableLiveData<AuthenticationState>()

    fun authenticateUser(email: String, password: String): LiveData<AuthenticationState> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authenticationState.postValue(AuthenticationState.Authenticating)
                val salt = fSource.getSaltByEmail(email)
                salt?.let {
                    val hashedPassword = HashPassword.hashPassword(password, salt)
                    val result = fAuth.signInWithEmailAndPassword(email, hashedPassword).await()
                    if (result.user != null) {
                        authenticationState.postValue(AuthenticationState.Authenticated)
                    } else {
                        authenticationState.postValue(AuthenticationState.Unauthenticated)
                    }
                } ?: run {
                    authenticationState.postValue(AuthenticationState.Unauthenticated)
                }
            } catch (e: Exception) {
                authenticationState.postValue(AuthenticationState.Unauthenticated)
            }
        }
        return authenticationState
    }

    fun autoAuthenticateFromCache(userViewModel: UserViewModel): LiveData<AuthenticationState> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cachedUser = userViewModel.getUserFromCache().firstOrNull()
                if (cachedUser == null) {
                    authenticationState.postValue(AuthenticationState.Unauthenticated)
                    return@launch
                }

                val firestoreUser = fSource.getLoginCredentials(cachedUser.userId)
                if (firestoreUser == null || cachedUser.passwordHash != firestoreUser.passwordHash) {
                    authenticationState.postValue(AuthenticationState.Unauthenticated)
                    return@launch
                }

                val result = fAuth.signInWithEmailAndPassword(firestoreUser.userEmail, cachedUser.passwordHash).await()
                if (result.user != null) {
                    authenticationState.postValue(AuthenticationState.Authenticated)
                } else {
                    authenticationState.postValue(AuthenticationState.Unauthenticated)
                }
            } catch (exception: Exception) {
                authenticationState.postValue(AuthenticationState.Unauthenticated)
            }
        }
        return authenticationState
    }
}