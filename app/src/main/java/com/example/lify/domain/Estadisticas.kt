package com.example.lify.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/** Periodos del filtro de estadísticas. */
enum class Periodo {
    SIETE_DIAS, TREINTA_DIAS, SEIS_MESES, UN_ANIO, TODO;

    /** Primer día incluido en el periodo (null = sin límite). */
    fun inicio(hoy: LocalDate): LocalDate? = when (this) {
        SIETE_DIAS -> hoy.minusDays(6)
        TREINTA_DIAS -> hoy.minusDays(29)
        SEIS_MESES -> hoy.minusMonths(6)
        UN_ANIO -> hoy.minusYears(1)
        TODO -> null
    }

    /** Instante (ms) a partir del cual cuentan los entrenamientos. 0 = todos. */
    fun desdeMillis(hoy: LocalDate, zona: ZoneId): Long =
        inicio(hoy)?.atStartOfDay(zona)?.toInstant()?.toEpochMilli() ?: 0L
}

/** Agregado por grupo muscular (series hechas y volumen en kg·rep). */
data class GrupoVolumen(val grupo: GrupoMuscular, val series: Int, val volumenKg: Double)

data class PorcentajeGrupo(val grupo: GrupoMuscular, val series: Int, val porcentaje: Float)

/** Una serie registrada, con la sesión a la que pertenece. */
data class SerieHistorial(val sesionId: Long, val fecha: Long, val peso: Double, val repeticiones: Int)

/** Resumen de un ejercicio en un entrenamiento concreto. */
data class PuntoProgreso(val fecha: Long, val pesoMaximo: Double, val volumen: Double, val unRmEstimado: Double)

fun fechaLocal(millis: Long, zona: ZoneId): LocalDate = Instant.ofEpochMilli(millis).atZone(zona).toLocalDate()

private fun lunesDe(fecha: LocalDate): LocalDate = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

/** Siete booleanos (lunes..domingo) de la semana actual: true si ese día hubo entrenamiento. */
fun diasEntrenadosSemana(inicios: List<Long>, hoy: LocalDate, zona: ZoneId): List<Boolean> {
    val lunes = lunesDe(hoy)
    val fechas = inicios.map { fechaLocal(it, zona) }.toSet()
    return (0..6).map { fechas.contains(lunes.plusDays(it.toLong())) }
}

/**
 * Racha en semanas consecutivas con al menos un entrenamiento. Si esta semana todavía no se ha
 * entrenado, la racha no se rompe: se cuenta desde la semana anterior.
 */
fun rachaSemanas(inicios: List<Long>, hoy: LocalDate, zona: ZoneId): Int {
    val semanas = inicios.map { lunesDe(fechaLocal(it, zona)) }.toSet()
    val actual = lunesDe(hoy)
    var cursor = if (semanas.contains(actual)) actual else actual.minusWeeks(1)
    var racha = 0
    while (semanas.contains(cursor)) {
        racha++
        cursor = cursor.minusWeeks(1)
    }
    return racha
}

/** Reparto porcentual de series por grupo muscular, de mayor a menor. */
fun distribucionPorGrupo(datos: List<GrupoVolumen>): List<PorcentajeGrupo> {
    val fusionado = datos.groupBy { it.grupo }.map { (g, lista) -> g to lista.sumOf { it.series } }
    val total = fusionado.sumOf { it.second }
    if (total <= 0) return emptyList()
    return fusionado
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
        .map { PorcentajeGrupo(it.first, it.second, it.second * 100f / total) }
}

/** Intensidad 0..1 de cada grupo (relativa al más trabajado), para colorear el mapa de músculos. */
fun intensidadMusculos(distribucion: List<PorcentajeGrupo>): Map<GrupoMuscular, Float> {
    val max = distribucion.maxOfOrNull { it.series } ?: return emptyMap()
    if (max <= 0) return emptyMap()
    return distribucion.associate { it.grupo to it.series.toFloat() / max }
}

/** 1RM estimado con la fórmula de Epley. Con 1 repetición el 1RM es el propio peso. */
fun unRmEstimado(peso: Double, repeticiones: Int): Double = when {
    repeticiones <= 0 || peso <= 0.0 -> 0.0
    repeticiones == 1 -> peso
    else -> peso * (1.0 + repeticiones / 30.0)
}

/** Agrupa las series por entrenamiento y calcula peso máximo, volumen y 1RM estimado de cada uno. */
fun progresoPorSesion(series: List<SerieHistorial>): List<PuntoProgreso> =
    series.groupBy { it.sesionId }
        .map { (_, lista) ->
            PuntoProgreso(
                fecha = lista.minOf { it.fecha },
                pesoMaximo = lista.maxOf { it.peso },
                volumen = lista.sumOf { it.peso * it.repeticiones },
                unRmEstimado = lista.maxOf { unRmEstimado(it.peso, it.repeticiones) }
            )
        }
        .sortedBy { it.fecha }
