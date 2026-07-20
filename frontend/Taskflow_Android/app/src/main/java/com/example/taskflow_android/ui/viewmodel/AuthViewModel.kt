package com.example.taskflow_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow_android.data.datastore.SessionManager
import com.example.taskflow_android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val username: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.first()
            val rememberMe = sessionManager.rememberMeFlow.first()

            val isTokenValid = !token.isNullOrEmpty()

            if (rememberMe && isTokenValid) {
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true
                )
            } else if (!rememberMe && isTokenValid) {
                // Si no se marcó "recordarme", nos aseguramos de que no haya sesión remanente al iniciar
                authRepository.logout()
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = false
                )
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Las contraseñas no coinciden"
            )
            return
        }

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Todos los campos son requeridos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.register(
                name = name.trim(),
                email = email.trim(),
                password = password.trim()
            )
            result.onSuccess { authResponse ->
                authRepository.saveSession(
                    response = authResponse,
                    rememberMe = true
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    username = authResponse.name,
                    successMessage = "Registro exitoso"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error en el registro"
                )
            }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Correo y contraseña requeridos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.login(email.trim(), password.trim())
            result.onSuccess { authResponse ->
                authRepository.saveSession(
                    response = authResponse,
                    rememberMe = rememberMe
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    username = authResponse.name,
                    successMessage = "Inicio de sesión exitoso"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Error en el inicio de sesión"
                )
            }
        }
    }

    fun logout() {
        // Resetear estado inmediatamente para evitar redirecciones erróneas en la UI
        _uiState.value = _uiState.value.copy(isAuthenticated = false)
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun updateRememberMe(rememberMe: Boolean) {
        viewModelScope.launch {
            sessionManager.setRememberMe(rememberMe)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
