package com.example.basicasistgoogle.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

@Composable
fun DropDownMenuMain(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Button(onClick = { expanded = true } ){
            Text ("Opciones")
            DropdownMenu(expanded, onDismissRequest = {expanded=false}) {
                DropdownMenuItem(text = {Text("Opcion1")}, onClick = {expanded=false})
                DropdownMenuItem(text = {Text("Opcion2")}, onClick = {expanded=false})
            }
        }
    }
}