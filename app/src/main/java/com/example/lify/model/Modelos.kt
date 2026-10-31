package com.example.lify.model

import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.TipoMedida

/** Ejercicio del catálogo (un único modelo para todo el proyecto). */
data class Ejercicio(
    val id: Int,
    val nombre: String,
    val grupo: GrupoMuscular,
    val descripcion: String,
    val favorito: Boolean = false
)

/** Ejercicio dentro de una rutina, con su configuración (el peso va siempre en kg). */
data class EjercicioEnRutina(
    val ejercicioId: Int,
    val nombre: String,
    val grupo: GrupoMuscular,
    val series: Int,
    val peso: Double,
    val repeticiones: Int
)

data class Rutina(
    val id: Long,
    val nombre: String,
    val descansoSegundos: Int,
    val ejercicios: List<EjercicioEnRutina>
)

/** Lo que se guarda de cada ejercicio al crear/editar una rutina. */
data class ConfigEjercicio(
    val ejercicioId: Int,
    val series: Int,
    val peso: Double,
    val repeticiones: Int
)

/** Entrenamiento terminado y guardado. */
data class Sesion(
    val id: Long,
    val rutinaId: Long?,
    val nombre: String,
    val inicio: Long,
    val fin: Long
)

/** Una serie realizada (peso en kg). */
data class SerieHecha(val peso: Double, val repeticiones: Int)

data class EjercicioRealizado(val ejercicioId: Int, val series: List<SerieHecha>)

data class Medida(val id: Long, val tipo: TipoMedida, val valor: Double, val fecha: Long)

// ---------------------------------------------------------------------------------------------
// Entrenamiento en curso. Se serializa con Gson como borrador (sobrevive si Android cierra la app),
// por eso solo usa tipos simples (String, Long, Int, listas) y el peso se guarda como texto en la
// unidad elegida al empezar: así se puede escribir/borrar con libertad en los campos.
// ---------------------------------------------------------------------------------------------

data class SerieActiva(
    val id: Long,
    val peso: String = "",
    val reps: String = "",
    val completada: Boolean = false
)

data class EjercicioActivo(
    val clave: Long,
    val ejercicioId: Int,
    val nombre: String,
    val grupo: String,
    val series: List<SerieActiva>,
    /** Series del último entrenamiento en el que se hizo este ejercicio (peso en kg). */
    val referencia: List<SerieHecha> = emptyList()
)

data class SesionActiva(
    val rutinaId: Long?,
    val nombre: String,
    val inicio: Long,
    val descansoSegundos: Int,
    /** Nombre de [com.example.lify.domain.UnidadPeso] con la que se escriben los pesos. */
    val unidad: String,
    val ejercicios: List<EjercicioActivo>,
    /** Instante (ms) en que acaba el descanso en curso; null si no hay temporizador activo. */
    val descansoFin: Long? = null
)
