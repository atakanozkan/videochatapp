package com.videochat.viewModel

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.videochat.base.BaseViewModel
import com.videochat.common.extension.HashPassword
import com.videochat.common.extension.UniqueIdGenerator.generateUniqueId
import com.videochat.data.source.FirestoreSource
import com.videochat.domain.model.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class VideoChatRegisterViewModel @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fSource: FirestoreSource
) : BaseViewModel<UiState>() {

    fun registerUser(email: String, username: String, password: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!validateUsername(username) || !validatePassword(password)) {
                    throw IllegalArgumentException("Validation failed")
                }

                val generatedSalt = HashPassword.generateSalt()
                val hashedPassword = HashPassword.hashPassword(password, generatedSalt)
                fAuth.createUserWithEmailAndPassword(email, hashedPassword).await()
                val uniqueClientUID = generateUniqueId()
                val userId = fAuth.currentUser?.uid ?: throw Exception("Failed to create user account.")

                val registrationSuccess = fSource.registerUser(
                    userId, username, email, hashedPassword, generatedSalt, uniqueClientUID
                )

                if (registrationSuccess) {
                    viewModelScope.launch {
                        _uiState.postValue(UiState.Success)
                    }
                } else {
                    viewModelScope.launch {
                        _uiState.postValue(UiState.Error("Failed to save user data"))
                    }
                }

            }
            catch (e: IllegalArgumentException) {
                viewModelScope.launch {
                    _uiState.postValue(UiState.Error(e.message ?: "Input validation failed"))
                }
            }
            catch (e: Exception) {
                viewModelScope.launch {
                    _uiState.postValue(UiState.Error(e.message ?: "Registration failed"))
                }
            }
        }
    }

    private fun validateUsername(username: String): Boolean =
        username.length >= 6 && username.any { it.isDigit() } && username.any { it.isLetter() }

    private fun validatePassword(password: String): Boolean =
        password.length >= 6
}