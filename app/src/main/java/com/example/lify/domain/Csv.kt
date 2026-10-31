package com.example.lify.domain

import java.util.Locale

/** Una fila del CSV de exportación (una serie realizada). */
data class FilaCsv(
    val fecha: String,
    val entrenamiento: String,
    val ejercicio: String,
    val grupo: String,
    val serie: Int,
    val pesoKg: Double,
    val repeticiones: Int
)

private fun escaparCsv(valor: String): String {
    val necesitaComillas = valor.any { it == ',' || it == '"' || it == '\n' || it == '\r' || it == ';' }
    return if (necesitaComillas) "\"" + valor.replace("\"", "\"\"") + "\"" else valor
}

/** Genera un CSV (separador coma, punto decimal) listo para abrir en Excel/Hojas de cálculo. */
fun generarCsv(filas: List<FilaCsv>): String {
    val sb = StringBuilder()
    sb.append("fecha,entrenamiento,ejercicio,grupo,serie,peso_kg,repeticiones\n")
    for (f in filas) {
        sb.append(escaparCsv(f.fecha)).append(',')
            .append(escaparCsv(f.entrenamiento)).append(',')
            .append(escaparCsv(f.ejercicio)).append(',')
            .append(escaparCsv(f.grupo)).append(',')
            .append(f.serie).append(',')
            .append(String.format(Locale.US, "%.2f", f.pesoKg)).append(',')
            .append(f.repeticiones).append('\n')
    }
    return sb.toString()
}
