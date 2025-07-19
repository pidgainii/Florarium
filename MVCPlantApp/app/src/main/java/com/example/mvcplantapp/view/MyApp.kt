package com.example.mvcplantapp.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.mvcplantapp.view_model.PlantViewModel
import com.example.mvcplantapp.view_model.Screen

@Composable
fun AppNavigation(viewModel: PlantViewModel) {
    // Observe the current screen in the ViewModel
    val currentScreen by viewModel.currentScreen.observeAsState(Screen.MainScreen)

    // Display the corresponding composable based on the screen state
    when (currentScreen) {
        Screen.AuthenticationScreen -> ViewAuthenticationScreen(viewModel) // Authentication screen
        Screen.MainScreen -> ViewMainScreen(viewModel)  // Main screen
        Screen.AddPlantScreen -> ViewAddPlantScreen(viewModel)  // New plant screen
        Screen.ViewPlantScreen -> ViewCheckPlantScreen(viewModel)  // Plant details screen
        Screen.EditPlantScreen -> ViewEditPlantScreen(viewModel)  // Plant details screen
        else -> {
            //temporal
            ViewMainScreen(viewModel)  // Main screen as default
        }
    }
}
