package com.example.lify.data

import com.example.lify.ui.screens.EjercicioSeleccionado

data class Rutina(
    val nombre: String,
    val ejercicios: List<EjercicioSeleccionado>
)