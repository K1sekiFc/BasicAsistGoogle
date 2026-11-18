package com.example.basicasistgoogle.presentation.screen.mainContainer.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicasistgoogle.domain.entity.UserData
import com.example.basicasistgoogle.domain.repository.AuthRepository
//import com.example.basicasistgoogle.domain.usecase.UseCaseGetAllProducts
//import com.example.basicasistgoogle.domain.util.ErrorHandler
import com.example.basicasistgoogle.presentation.screen.mainContainer.profile.ProfileUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())

    val uiState: StateFlow<MainUiState> = _uiState
    init {
        loadSessionUser()
    }
    private fun loadSessionUser() {
        val user = authRepository.getSessionUser()
        _uiState.update { it.copy(userData = user) }
    }
    fun submitAttendanceRecord(userName: String, email: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = "",
                    isSuccess = false,
                    lastLatitude = latitude,
                    lastLongitude = longitude
                )
            }
            authRepository.submitAttendance(userName, email, latitude, longitude).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Error de red"
                    )
                }
            }
        }
    }

}

data class MainUiState(
    val userData: UserData? =null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val lastLongitude: Double? =null,
    val lastLatitude: Double? =null,
    )
