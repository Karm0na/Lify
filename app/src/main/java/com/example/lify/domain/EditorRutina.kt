package com.example.lify.domain

import com.example.lify.model.ConfigEjercicio
import com.example.lify.model.Rutina

/** Duraciones de descanso disponibles (0 = temporizador apagado). De 30 s a 5 min. */
val OPCIONES_DESCANSO: List<Int> = listOf(0, 30, 45, 60, 75, 90, 120, 150, 180, 210, 240, 270, 300)

enum class ErrorEditor { SIN_NOMBRE, SIN_EJERCICIOS }

/** Ejercicio mientras se edita una rutina: los campos son texto para poder escribir/borrar sin problemas. */
data class EjercicioEditable(
    val clave: Long,
    val ejercicioId: Int,
    val nombre: String,
    val grupo: GrupoMuscular,
    val series: String,
    val peso: String,
    val repeticiones: String
)

data class EditorRutinaState(
    val rutinaId: Long? = null,
    val nombre: String = "",
    val descansoSegundos: Int = 0,
    val ejercicios: List<EjercicioEditable> = emptyList(),
    val unidad: UnidadPeso = UnidadPeso.KG
)

fun EditorRutinaState.validar(): ErrorEditor? = when {
    nombre.isBlank() -> ErrorEditor.SIN_NOMBRE
    ejercicios.isEmpty() -> ErrorEditor.SIN_EJERCICIOS
    else -> null
}

/** Convierte lo escrito en la configuración que se guarda (peso en kg, valores acotados). */
fun EditorRutinaState.aConfig(): List<ConfigEjercicio> = ejercicios.map {
    ConfigEjercicio(
        ejercicioId = it.ejercicioId,
        series = (parsearNumero(it.series)?.toInt() ?: 1).coerceIn(1, 20),
        peso = unidad.aKg(parsearNumero(it.peso) ?: 0.0),
        repeticiones = (parsearNumero(it.repeticiones)?.toInt() ?: 0).coerceIn(0, 999)
    )
}

fun Rutina.aEditor(unidad: UnidadPeso): EditorRutinaState = EditorRutinaState(
    rutinaId = id,
    nombre = nombre,
    descansoSegundos = descansoSegundos,
    unidad = unidad,
    ejercicios = ejercicios.map {
        EjercicioEditable(
            clave = nuevoId(),
            ejercicioId = it.ejercicioId,
            nombre = it.nombre,
            grupo = it.grupo,
            series = it.series.toString(),
            peso = if (it.peso > 0.0) formatearNumero(unidad.desdeKg(it.peso)) else "",
            repeticiones = if (it.repeticiones > 0) it.repeticiones.toString() else ""
        )
    }
)

/** Sube (delta = -1) o baja (delta = +1) un ejercicio en la lista. */
fun EditorRutinaState.mover(clave: Long, delta: Int): EditorRutinaState {
    val i = ejercicios.indexOfFirst { it.clave == clave }
    val j = i + delta
    if (i < 0 || j < 0 || j >= ejercicios.size) return this
    val lista = ejercicios.toMutableList()
    val tmp = lista[i]
    lista[i] = lista[j]
    lista[j] = tmp
    return copy(ejercicios = lista)
}
