package com.example.lify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.domain.EditorRutinaState
import com.example.lify.domain.EjercicioEditable
import com.example.lify.domain.ErrorEditor
import com.example.lify.domain.aConfig
import com.example.lify.domain.aEditor
import com.example.lify.domain.filtrarDecimal
import com.example.lify.domain.filtrarEntero
import com.example.lify.domain.mover
import com.example.lify.domain.nuevoId
import com.example.lify.domain.validar
import com.example.lify.model.Rutina
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Borrador de la rutina que se está creando o editando. Es compartido entre el selector de
 * ejercicios y la pantalla de configuración: así no hace falta pasar listas por la navegación.
 */
class EditorRutinaViewModel(app: Application) : AndroidViewModel(app) {
    private val aplicacion = getApplication<LifyApp>()
    private val repo = aplicacion.repositorio

    private val _estado = MutableStateFlow(EditorRutinaState())
    val estado: StateFlow<EditorRutinaState> = _estado.asStateFlow()

    fun nueva() {
        _estado.value = EditorRutinaState(unidad = aplicacion.preferencias.unidad.value)
    }

    fun cargar(rutina: Rutina) {
        _estado.value = rutina.aEditor(aplicacion.preferencias.unidad.value)
    }

    fun cambiarNombre(valor: String) = _estado.update { it.copy(nombre = valor.take(60)) }

    fun cambiarDescanso(segundos: Int) = _estado.update { it.copy(descansoSegundos = segundos) }

    fun cambiarSeries(clave: Long, texto: String) =
        actualizar(clave) { it.copy(series = filtrarEntero(texto, 2)) }

    fun cambiarPeso(clave: Long, texto: String) =
        actualizar(clave) { it.copy(peso = filtrarDecimal(texto)) }

    fun cambiarRepeticiones(clave: Long, texto: String) =
        actualizar(clave) { it.copy(repeticiones = filtrarEntero(texto, 3)) }

    fun quitar(clave: Long) = _estado.update { st -> st.copy(ejercicios = st.ejercicios.filter { it.clave != clave }) }

    fun mover(clave: Long, delta: Int) = _estado.update { it.mover(clave, delta) }

    /** Añade los ejercicios elegidos en el selector (ignora los que ya estaban). */
    fun agregarEjercicios(ids: List<Int>) {
        viewModelScope.launch {
            val nuevos = repo.ejerciciosPorIds(ids)
            _estado.update { st ->
                val existentes = st.ejercicios.map { it.ejercicioId }.toSet()
                st.copy(
                    ejercicios = st.ejercicios + nuevos.filter { it.id !in existentes }.map {
                        EjercicioEditable(nuevoId(), it.id, it.nombre, it.grupo, "3", "", "10")
                    }
                )
            }
        }
    }

    /** Valida y guarda. [alTerminar] recibe null si todo fue bien o el motivo del error. */
    fun guardar(alTerminar: (ErrorEditor?) -> Unit) {
        val st = _estado.value
        val error = st.validar()
        if (error != null) {
            alTerminar(error)
            return
        }
        viewModelScope.launch {
            repo.guardarRutina(st.rutinaId, st.nombre, st.descansoSegundos, st.aConfig())
            alTerminar(null)
        }
    }

    private fun actualizar(clave: Long, f: (EjercicioEditable) -> EjercicioEditable) {
        _estado.update { st -> st.copy(ejercicios = st.ejercicios.map { if (it.clave == clave) f(it) else it }) }
    }
}
