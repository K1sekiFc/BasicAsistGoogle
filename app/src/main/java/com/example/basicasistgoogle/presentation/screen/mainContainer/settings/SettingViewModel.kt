package com.example.basicasistgoogle.presentation.screen.mainContainer.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor()
: ViewModel(){
    private val _uiState = MutableStateFlow(SettingUIState())
    val uiState : StateFlow<SettingUIState> = _uiState
}
data class SettingUIState(

    val config: String = ""
)