package com.example.lify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.Periodo
import com.example.lify.domain.PorcentajeGrupo
import com.example.lify.domain.PuntoProgreso
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.diasEntrenadosSemana
import com.example.lify.domain.fechaLocal
import com.example.lify.domain.rachaSemanas
import com.example.lify.domain.distribucionPorGrupo
import com.example.lify.domain.intensidadMusculos
import com.example.lify.domain.progresoPorSesion
import com.example.lify.model.Ejercicio
import com.example.lify.model.Sesion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class EstadisticasUi(
    val diasSemana: List<Boolean> = List(7) { false },
    val entrenamientosPeriodo: Int = 0,
    val distribucion: List<PorcentajeGrupo> = emptyList(),
    val intensidad: Map<GrupoMuscular, Float> = emptyMap(),
    val recientes: List<Sesion> = emptyList(),
    /** Todos los días (fecha local) en los que hubo al menos un entrenamiento: alimenta el calendario. */
    val fechasEntrenadas: Set<LocalDate> = emptySet(),
    val rachaSemanas: Int = 0,
    val totalEntrenamientos: Int = 0,
    /** Volumen total (kg · repeticiones) del periodo elegido. */
    val volumenPeriodo: Double = 0.0
)

/** Todo lo que se ve en la pestaña Estadísticas sale de consultas a la base de datos. */
@OptIn(ExperimentalCoroutinesApi::class)
class EstadisticasViewModel(app: Application) : AndroidViewModel(app) {
    private val aplicacion = getApplication<LifyApp>()
    private val repo = aplicacion.repositorio

    private val _periodo = MutableStateFlow(Periodo.SIETE_DIAS)
    val periodo: StateFlow<Periodo> = _periodo.asStateFlow()

    val unidad: StateFlow<UnidadPeso> = aplicacion.preferencias.unidad

    val ui: StateFlow<EstadisticasUi> = combine(
        repo.sesiones,
        _periodo,
        _periodo.flatMapLatest { p ->
            repo.agregadoPorGrupo(p.desdeMillis(LocalDate.now(), ZoneId.systemDefault()))
        }
    ) { sesiones, p, agregado ->
        val zona = ZoneId.systemDefault()
        val hoy = LocalDate.now()
        val desde = p.desdeMillis(hoy, zona)
        val distribucion = distribucionPorGrupo(agregado)
        EstadisticasUi(
            diasSemana = diasEntrenadosSemana(sesiones.map { it.inicio }, hoy, zona),
            entrenamientosPeriodo = sesiones.count { it.inicio >= desde },
            distribucion = distribucion,
            intensidad = intensidadMusculos(distribucion),
            recientes = sesiones.take(10),
            fechasEntrenadas = sesiones.map { fechaLocal(it.inicio, zona) }.toSet(),
            rachaSemanas = rachaSemanas(sesiones.map { it.inicio }, hoy, zona),
            totalEntrenamientos = sesiones.size,
            volumenPeriodo = agregado.sumOf { it.volumenKg }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EstadisticasUi())

    // ---- progreso por ejercicio

    val ejerciciosRealizados: StateFlow<List<Ejercicio>> =
        repo.ejerciciosRealizados.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _ejercicioElegido = MutableStateFlow<Int?>(null)

    /** El elegido por el usuario o, si no ha elegido, el primero con historial. */
    val ejercicioActual: StateFlow<Int?> = combine(_ejercicioElegido, ejerciciosRealizados) { elegido, lista ->
        if (elegido != null && lista.any { it.id == elegido }) elegido else lista.firstOrNull()?.id
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val progreso: StateFlow<List<PuntoProgreso>> = ejercicioActual.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repo.historialEjercicio(id).map { progresoPorSesion(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarPeriodo(p: Periodo) {
        _periodo.value = p
    }

    fun elegirEjercicio(id: Int) {
        _ejercicioElegido.value = id
    }

    fun eliminarSesion(id: Long) {
        viewModelScope.launch { repo.eliminarSesion(id) }
    }
}
