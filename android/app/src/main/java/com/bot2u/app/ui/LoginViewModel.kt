package com.bot2u.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Login screen
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = ""
)

/**
 * ViewModel for Login functionality
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Attempt to log in with email and password
     */
    fun login(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Simulate login - replace with actual authentication logic
        // For demo purposes, accept any non-empty credentials
        if (email.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch {
                // Simulate network delay
                delay(1500)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        email = email
                    )
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Please enter valid credentials"
                )
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Logout user
     */
    fun logout() {
        _uiState.update {
            LoginUiState()
        }
    }
}
