package com.example.lify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.model.Ejercicio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Catálogo de ejercicios (buscador, filtros y favoritos). */
class EjerciciosViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = getApplication<LifyApp>().repositorio

    val ejercicios: StateFlow<List<Ejercicio>> =
        repo.ejercicios.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun alternarFavorito(ejercicio: Ejercicio) {
        viewModelScope.launch { repo.alternarFavorito(ejercicio.id, !ejercicio.favorito) }
    }
}
