package com.example.lify.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun RutinasScreen(navController: NavController) {
    // Aquí construyes la UI para la pantalla de Rutinas
    Text(text = "Pantalla de Rutinas")

    // Botón para navegar a Añadir Rutina
    Button(onClick = {
        navController.navigate("añadir_rutina")
    }) {
        Text(text = "Añadir Rutina")
    }
}
