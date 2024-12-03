package com.example.lify.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.lify.data.Rutina

class RutinasViewModel : ViewModel() {
    // Lista dinámica para almacenar las rutinas
    val rutinas = mutableStateListOf<Rutina>()

    // Función para añadir una rutina
    fun agregarRutina(rutina: Rutina) {
        rutinas.add(rutina)
    }
}