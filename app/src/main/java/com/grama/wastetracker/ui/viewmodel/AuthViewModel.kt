package com.grama.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.User
import com.grama.wastetracker.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isRegistered: Boolean = false,
    val passwordResetSent: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val currentUser = authRepository.currentUser
        if (currentUser != null) {
            _uiState.update { it.copy(isLoggedIn = true, isLoading = true) }
            viewModelScope.launch {
                authRepository.getUserData(currentUser.uid).fold(
                    onSuccess = { user ->
                        _uiState.update { it.copy(user = user, isLoading = false) }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.login(email, password).fold(
                onSuccess = { firebaseUser ->
                    authRepository.getUserData(firebaseUser.uid).fold(
                        onSuccess = { user ->
                            _uiState.update {
                                it.copy(isLoggedIn = true, user = user, isLoading = false)
                            }
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(isLoggedIn = true, isLoading = false)
                            }
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: "Login failed")
                    }
                }
            )
        }
    }

    fun register(fullName: String, email: String, password: String, phone: String, village: String, role: String = User.ROLE_CITIZEN) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all required fields") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val user = User(
                fullName = fullName,
                phone = phone,
                villageName = village,
                role = role
            )
            authRepository.register(email, password, user).fold(
                onSuccess = { firebaseUser ->
                    authRepository.getUserData(firebaseUser.uid).fold(
                        onSuccess = { userData ->
                            _uiState.update {
                                it.copy(isLoggedIn = true, user = userData, isLoading = false, isRegistered = true)
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(isLoggedIn = true, isLoading = false, isRegistered = true)
                            }
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: "Registration failed")
                    }
                }
            )
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your email") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.resetPassword(email).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, passwordResetSent = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: "Failed to send reset email")
                    }
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update { AuthUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun isAdmin(): Boolean = _uiState.value.user?.role == User.ROLE_ADMIN
}
