package com.example.basicasistgoogle.presentation.screen.mainContainer.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basicasistgoogle.R
import com.example.basicasistgoogle.presentation.component.AppTopBarDef
import com.example.basicasistgoogle.presentation.component.GradientBackground

@Composable
fun SettingScreen(settingViewModel: SettingViewModel = hiltViewModel(),navigateBack:() -> Unit) {

    val uiState by settingViewModel.uiState.collectAsStateWithLifecycle()

    GradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar={
                AppTopBarDef(
                    title = stringResource(R.string.setting_screen_button_profile), subtitle={}, showBackButton = true,
                    onBackClick = navigateBack
                )
            },
             containerColor = Color.Transparent
        ) {padding ->
            Column( modifier = Modifier.padding(20.dp).padding(padding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.setting_screen_button_profile),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}