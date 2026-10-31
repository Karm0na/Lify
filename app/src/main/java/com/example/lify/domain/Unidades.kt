package com.example.lify.domain

import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToLong

/** Unidad de peso que ve el usuario. Internamente todo se guarda en kilogramos. */
enum class UnidadPeso(val simbolo: String, private val porKg: Double) {
    KG("kg", 1.0),
    LB("lb", 2.2046226218);

    fun desdeKg(kg: Double): Double = kg * porKg
    fun aKg(valor: Double): Double = valor / porKg

    companion object {
        fun desdeNombre(nombre: String?): UnidadPeso = values().firstOrNull { it.name == nombre } ?: KG
    }
}

/** Formatea un número sin ceros sobrantes ("80", "82.5") y siempre con punto decimal. */
fun formatearNumero(valor: Double, decimales: Int = 1): String {
    if (valor.isNaN() || valor.isInfinite()) return "0"
    val factor = 10.0.pow(decimales)
    val r = (valor * factor).roundToLong() / factor
    if (r == r.toLong().toDouble()) return r.toLong().toString()
    return String.format(Locale.US, "%.${decimales}f", r).trimEnd('0').trimEnd('.')
}

/** Lee un número escrito por el usuario (acepta coma o punto). Devuelve null si está vacío o es inválido/negativo. */
fun parsearNumero(texto: String): Double? {
    val v = texto.trim().replace(',', '.').toDoubleOrNull() ?: return null
    return if (v.isNaN() || v.isInfinite() || v < 0.0) null else v
}

/** Formatea segundos como mm:ss (o h:mm:ss a partir de una hora). */
fun formatearDuracion(segundos: Long): String {
    val s = if (segundos < 0) 0 else segundos
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
    else String.format(Locale.US, "%02d:%02d", m, sec)
}
