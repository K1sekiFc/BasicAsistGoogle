package com.example.basicasistgoogle.presentation.screen.login

import android.content.Context
//
//import android.widget.Toast
//import androidx.credentials.CredentialManager
//
//import androidx.credentials.CustomCredential
//import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicasistgoogle.domain.entity.UserData
import com.example.basicasistgoogle.domain.repository.AuthRepository
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onUserChanged(user: String) {
        _uiState.update { state -> state.copy(user = user) }
        verifyLogin()
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
        verifyLogin()
    }

    fun onClickGoogleLogin(activityContext: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            val result = authRepository.loginWithGoogle(activityContext)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { userData ->
                    _uiState.update {
                        it.copy(
                            user = userData.email,
                            userData =userData,
                            isLoginSuccess = true
                        )
                    }
                    _navigationEvent.emit(Unit)

            }.onFailure { error->
                _uiState.update { it.copy(error = error.localizedMessage ?: "Error al autenticar con Google") }
        }
    }
    }

    fun onClickSelected() {
        _uiState.update {
            it.copy(error = "Por ahora, inicia sesión con Google.")
        }
    }

    private fun verifyLogin() {
        val enabledLogin = isEmailValid(_uiState.value.user) && isPasswordValid(_uiState.value.password)
        _uiState.update { it.copy(isLoginEnabled = enabledLogin) }
    }

    private fun isEmailValid(user: String): Boolean = user.isNotBlank()
    private fun isPasswordValid(password: String): Boolean = password.length >= 6
}

data class LoginUiState(
    val user: String = "",
    var password: String = "",
    val userData: UserData? =null,
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val error: String = ""
)
