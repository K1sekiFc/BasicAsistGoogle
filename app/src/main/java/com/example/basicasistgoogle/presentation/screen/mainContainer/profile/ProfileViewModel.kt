package com.example.basicasistgoogle.presentation.screen.mainContainer.profile

import androidx.lifecycle.ViewModel
import com.example.basicasistgoogle.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = authRepository.getSessionUser()
        _uiState.value = ProfileUIState(
            name = user?.displayName.orEmpty(),
            email = user?.email.orEmpty()
        )
    }
}

data class ProfileUIState(
    val name: String = "",
    val email: String = "",
    val password: String = "******",
    val username: String = "",
)