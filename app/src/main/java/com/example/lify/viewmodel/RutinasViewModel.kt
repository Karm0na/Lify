package com.example.lify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.data.RutinaPreconfigurada
import com.example.lify.model.ConfigEjercicio
import com.example.lify.model.Rutina
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lista de rutinas guardadas en la base de datos (compartida por todas las pantallas). */
class RutinasViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = getApplication<LifyApp>().repositorio

    val rutinas: StateFlow<List<Rutina>> =
        repo.rutinas.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun eliminar(id: Long) {
        viewModelScope.launch { repo.eliminarRutina(id) }
    }

    fun agregarPreconfigurada(nombre: String, plantilla: RutinaPreconfigurada) {
        viewModelScope.launch {
            repo.asegurarCatalogo()
            repo.guardarRutina(
                id = null,
                nombre = nombre,
                descansoSegundos = plantilla.descansoSegundos,
                ejercicios = plantilla.ejercicios.map { ConfigEjercicio(it.ejercicioId, it.series, 0.0, it.repeticiones) }
            )
        }
    }
}
