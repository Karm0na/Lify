package com.example.lify.domain

import com.example.lify.model.EjercicioActivo
import com.example.lify.model.EjercicioRealizado
import com.example.lify.model.SerieActiva
import com.example.lify.model.SerieHecha
import com.example.lify.model.SesionActiva
import java.util.UUID

/** Lógica pura (sin Android) del entrenamiento en curso. Todas las funciones devuelven una copia nueva. */

fun nuevoId(): Long = UUID.randomUUID().leastSignificantBits

fun crearEjercicioActivo(
    ejercicioId: Int,
    nombre: String,
    grupo: GrupoMuscular,
    series: Int,
    pesoKg: Double,
    repeticiones: Int,
    referencia: List<SerieHecha>,
    unidad: UnidadPeso
): EjercicioActivo {
    val pesoTxt = if (pesoKg > 0.0) formatearNumero(unidad.desdeKg(pesoKg)) else ""
    val repsTxt = if (repeticiones > 0) repeticiones.toString() else ""
    val filas = (0 until series.coerceAtLeast(1)).map { SerieActiva(nuevoId(), pesoTxt, repsTxt, false) }
    return EjercicioActivo(nuevoId(), ejercicioId, nombre, grupo.name, filas, referencia)
}

/** Serie del último entrenamiento que sirve de referencia para la fila [indice] (la última si no hay tantas). */
fun EjercicioActivo.referenciaDe(indice: Int): SerieHecha? = referencia.getOrNull(indice) ?: referencia.lastOrNull()

private fun SesionActiva.conEjercicio(clave: Long, f: (EjercicioActivo) -> EjercicioActivo): SesionActiva =
    copy(ejercicios = ejercicios.map { if (it.clave == clave) f(it) else it })

private fun EjercicioActivo.conSerie(serieId: Long, f: (SerieActiva) -> SerieActiva): EjercicioActivo =
    copy(series = series.map { if (it.id == serieId) f(it) else it })

fun SesionActiva.conPeso(clave: Long, serieId: Long, texto: String): SesionActiva =
    conEjercicio(clave) { e -> e.conSerie(serieId) { it.copy(peso = texto) } }

fun SesionActiva.conReps(clave: Long, serieId: Long, texto: String): SesionActiva =
    conEjercicio(clave) { e -> e.conSerie(serieId) { it.copy(reps = texto) } }

/**
 * Marca/desmarca una serie. Al marcarla arranca el temporizador de descanso. Si la serie no tiene
 * repeticiones se rellena con las del último entrenamiento; si tampoco hay referencia no se marca
 * (devuelve false en el segundo valor).
 */
fun SesionActiva.alternarSerie(clave: Long, serieId: Long, ahora: Long): Pair<SesionActiva, Boolean> {
    val ej = ejercicios.firstOrNull { it.clave == clave } ?: return this to false
    val indice = ej.series.indexOfFirst { it.id == serieId }
    if (indice < 0) return this to false
    val serie = ej.series[indice]

    if (serie.completada) {
        return conEjercicio(clave) { e -> e.conSerie(serieId) { it.copy(completada = false) } } to true
    }

    val unidadPeso = UnidadPeso.desdeNombre(unidad)
    val ref = ej.referenciaDe(indice)
    var reps = serie.reps
    var peso = serie.peso
    if ((parsearNumero(reps) ?: 0.0).toInt() <= 0) {
        if (ref != null && ref.repeticiones > 0) reps = ref.repeticiones.toString() else return this to false
    }
    if (peso.isBlank() && ref != null && ref.peso > 0.0) peso = formatearNumero(unidadPeso.desdeKg(ref.peso))

    val marcada = conEjercicio(clave) { e -> e.conSerie(serieId) { it.copy(peso = peso, reps = reps, completada = true) } }
    val fin = if (descansoSegundos > 0) ahora + descansoSegundos * 1000L else null
    return marcada.copy(descansoFin = fin) to true
}

/** Añade una serie al final copiando los valores de la anterior. */
fun SesionActiva.anadirSerie(clave: Long): SesionActiva =
    conEjercicio(clave) { e ->
        val ultima = e.series.lastOrNull()
        e.copy(series = e.series + SerieActiva(nuevoId(), ultima?.peso ?: "", ultima?.reps ?: "", false))
    }

fun SesionActiva.quitarSerie(clave: Long, serieId: Long): SesionActiva =
    conEjercicio(clave) { e -> e.copy(series = e.series.filter { it.id != serieId }) }

fun SesionActiva.quitarEjercicio(clave: Long): SesionActiva =
    copy(ejercicios = ejercicios.filter { it.clave != clave })

fun SesionActiva.anadirEjercicios(nuevos: List<EjercicioActivo>): SesionActiva {
    val existentes = ejercicios.map { it.ejercicioId }.toSet()
    return copy(ejercicios = ejercicios + nuevos.filter { it.ejercicioId !in existentes })
}

fun SesionActiva.saltarDescanso(): SesionActiva = copy(descansoFin = null)

fun SesionActiva.sumarDescanso(ahora: Long, segundos: Int): SesionActiva {
    val base = maxOf(descansoFin ?: ahora, ahora)
    return copy(descansoFin = base + segundos * 1000L)
}

/** Segundos que faltan de descanso (0 = terminó, null = sin temporizador). */
fun SesionActiva.descansoRestante(ahora: Long): Long? =
    descansoFin?.let { maxOf(0L, (it - ahora + 999L) / 1000L) }

fun SesionActiva.seriesCompletadas(): Int = ejercicios.sumOf { e -> e.series.count { it.completada } }

/** Series que se guardarán: solo las marcadas y con repeticiones. Los pesos vuelven a kg. */
fun SesionActiva.aRealizados(): List<EjercicioRealizado> {
    val u = UnidadPeso.desdeNombre(unidad)
    return ejercicios.mapNotNull { e ->
        val hechas = e.series.filter { it.completada }.mapNotNull { s ->
            val reps = (parsearNumero(s.reps) ?: 0.0).toInt()
            if (reps <= 0) null else SerieHecha(u.aKg(parsearNumero(s.peso) ?: 0.0), reps)
        }
        if (hechas.isEmpty()) null else EjercicioRealizado(e.ejercicioId, hechas)
    }
}
