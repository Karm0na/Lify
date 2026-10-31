package com.example.lify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.aRealizados
import com.example.lify.domain.alternarSerie
import com.example.lify.domain.anadirEjercicios
import com.example.lify.domain.anadirSerie
import com.example.lify.domain.conPeso
import com.example.lify.domain.conReps
import com.example.lify.domain.crearEjercicioActivo
import com.example.lify.domain.filtrarDecimal
import com.example.lify.domain.filtrarEntero
import com.example.lify.domain.quitarEjercicio
import com.example.lify.domain.quitarSerie
import com.example.lify.domain.saltarDescanso
import com.example.lify.domain.sumarDescanso
import com.example.lify.model.Rutina
import com.example.lify.model.SesionActiva
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Entrenamiento en curso. Cada cambio se guarda como borrador en las preferencias, así que si
 * Android cierra la app a mitad de entrenamiento se puede continuar donde se dejó.
 */
class SesionViewModel(app: Application) : AndroidViewModel(app) {
    private val aplicacion = getApplication<LifyApp>()
    private val repo = aplicacion.repositorio
    private val prefs = aplicacion.preferencias
    private val gson = Gson()

    private val _sesion = MutableStateFlow(leerBorrador())
    val sesion: StateFlow<SesionActiva?> = _sesion.asStateFlow()

    private var guardando = false

    private fun leerBorrador(): SesionActiva? {
        val json = aplicacion.preferencias.leerBorrador() ?: return null
        return try {
            gson.fromJson(json, SesionActiva::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun fijar(nueva: SesionActiva?) {
        _sesion.value = nueva
        prefs.guardarBorrador(nueva?.let { gson.toJson(it) })
    }

    private fun modificar(f: (SesionActiva) -> SesionActiva) {
        val actual = _sesion.value ?: return
        fijar(f(actual))
    }

    fun iniciarVacio(nombre: String) {
        fijar(
            SesionActiva(
                rutinaId = null,
                nombre = nombre,
                inicio = System.currentTimeMillis(),
                descansoSegundos = DESCANSO_POR_DEFECTO,
                unidad = prefs.unidad.value.name,
                ejercicios = emptyList()
            )
        )
    }

    /** Empieza el entrenamiento de una rutina, con las series configuradas y la referencia anterior. */
    fun iniciarRutina(rutina: Rutina, alTerminar: () -> Unit) {
        viewModelScope.launch {
            val unidad = prefs.unidad.value
            val ejercicios = rutina.ejercicios.map { e ->
                crearEjercicioActivo(
                    ejercicioId = e.ejercicioId,
                    nombre = e.nombre,
                    grupo = e.grupo,
                    series = e.series,
                    pesoKg = e.peso,
                    repeticiones = e.repeticiones,
                    referencia = repo.referenciaEjercicio(e.ejercicioId),
                    unidad = unidad
                )
            }
            fijar(
                SesionActiva(
                    rutinaId = rutina.id,
                    nombre = rutina.nombre,
                    inicio = System.currentTimeMillis(),
                    descansoSegundos = rutina.descansoSegundos,
                    unidad = unidad.name,
                    ejercicios = ejercicios
                )
            )
            alTerminar()
        }
    }

    /** Añade ejercicios al entrenamiento sobre la marcha (con una serie vacía cada uno). */
    fun agregarEjercicios(ids: List<Int>) {
        viewModelScope.launch {
            val actual = _sesion.value ?: return@launch
            val unidad = UnidadPeso.desdeNombre(actual.unidad)
            val nuevos = repo.ejerciciosPorIds(ids).map { e ->
                crearEjercicioActivo(
                    ejercicioId = e.id,
                    nombre = e.nombre,
                    grupo = e.grupo,
                    series = 1,
                    pesoKg = 0.0,
                    repeticiones = 0,
                    referencia = repo.referenciaEjercicio(e.id),
                    unidad = unidad
                )
            }
            modificar { it.anadirEjercicios(nuevos) }
        }
    }

    fun cambiarPeso(clave: Long, serieId: Long, texto: String) =
        modificar { it.conPeso(clave, serieId, filtrarDecimal(texto)) }

    fun cambiarReps(clave: Long, serieId: Long, texto: String) =
        modificar { it.conReps(clave, serieId, filtrarEntero(texto, 3)) }

    /** Marca/desmarca la serie. Devuelve false si no se pudo marcar (faltan repeticiones). */
    fun alternarSerie(clave: Long, serieId: Long): Boolean {
        val actual = _sesion.value ?: return false
        val (nueva, ok) = actual.alternarSerie(clave, serieId, System.currentTimeMillis())
        if (ok) fijar(nueva)
        return ok
    }

    fun anadirSerie(clave: Long) = modificar { it.anadirSerie(clave) }

    fun quitarSerie(clave: Long, serieId: Long) = modificar { it.quitarSerie(clave, serieId) }

    fun quitarEjercicio(clave: Long) = modificar { it.quitarEjercicio(clave) }

    fun saltarDescanso() = modificar { it.saltarDescanso() }

    fun sumarDescanso(segundos: Int) = modificar { it.sumarDescanso(System.currentTimeMillis(), segundos) }

    /**
     * Guarda el entrenamiento (solo las series marcadas). [alTerminar] recibe true si se guardó y
     * false si no había ninguna serie marcada (no se guarda nada).
     */
    fun terminar(alTerminar: (Boolean) -> Unit) {
        val actual = _sesion.value ?: return
        val realizados = actual.aRealizados()
        if (realizados.isEmpty()) {
            alTerminar(false)
            return
        }
        if (guardando) return
        guardando = true
        viewModelScope.launch {
            try {
                repo.guardarSesion(actual.rutinaId, actual.nombre, actual.inicio, System.currentTimeMillis(), realizados)
                fijar(null)
                alTerminar(true)
            } finally {
                guardando = false
            }
        }
    }

    fun descartar() = fijar(null)

    companion object {
        const val DESCANSO_POR_DEFECTO = 90
    }
}
