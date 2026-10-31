package com.example.lify.domain

import java.text.Normalizer
import java.util.Locale

/** Minúsculas y sin acentos, para que "biceps" encuentre "Bíceps". */
fun normalizarBusqueda(texto: String): String =
    Normalizer.normalize(texto.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

/** Deja solo dígitos y limita la longitud (series, repeticiones). */
fun filtrarEntero(texto: String, maxDigitos: Int): String = texto.filter { it.isDigit() }.take(maxDigitos)

/** Deja dígitos y un único separador decimal (coma o punto) para pesos y medidas. */
fun filtrarDecimal(texto: String, maxLongitud: Int = 7): String {
    val sb = StringBuilder()
    var haySeparador = false
    for (c in texto) {
        when {
            c.isDigit() -> sb.append(c)
            (c == '.' || c == ',') && !haySeparador -> {
                sb.append(c)
                haySeparador = true
            }
        }
    }
    return sb.toString().take(maxLongitud)
}
