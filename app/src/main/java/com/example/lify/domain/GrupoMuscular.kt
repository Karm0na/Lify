package com.example.lify.domain

import java.text.Normalizer
import java.util.Locale

/**
 * Lista cerrada de grupos musculares. Sustituye a las ~45 etiquetas libres del catálogo original
 * ("Core", "Core y cardio", "Core y oblicuos"...): cada ejercicio pertenece a UN grupo principal.
 */
enum class GrupoMuscular(val etiquetaCatalogo: String) {
    PECHO("Pecho"),
    ESPALDA("Espalda"),
    HOMBROS("Hombros"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    TRAPECIOS("Trapecios"),
    CORE("Core"),
    CUADRICEPS("Cuádriceps"),
    ISQUIOTIBIALES("Isquiotibiales"),
    GLUTEOS("Glúteos"),
    GEMELOS("Gemelos"),
    PIERNAS("Piernas"),
    CARDIO("Cardio");

    companion object {
        /** Convierte una etiqueta libre del catálogo ("Core y cardio", "Pectorales"...) en su grupo principal. */
        fun desdeEtiqueta(etiqueta: String): GrupoMuscular {
            val t = sinAcentos(etiqueta.trim().lowercase(Locale.ROOT))
            return when {
                t.startsWith("pec") -> PECHO
                t.startsWith("abdom") || t.startsWith("core") -> CORE
                t.startsWith("espalda") -> ESPALDA
                t.startsWith("hombro") -> HOMBROS
                t.startsWith("biceps") -> BICEPS
                t.startsWith("triceps") -> TRICEPS
                t.startsWith("trapecio") -> TRAPECIOS
                t.startsWith("cuadriceps") -> CUADRICEPS
                t.startsWith("isquio") -> ISQUIOTIBIALES
                t.startsWith("gluteo") -> GLUTEOS
                t.startsWith("gemelo") -> GEMELOS
                t.startsWith("pierna") -> PIERNAS
                t.startsWith("cardio") -> CARDIO
                else -> CORE
            }
        }

        /** Convierte el nombre guardado en base de datos (enum.name) en el grupo; CORE si es desconocido. */
        fun desdeNombre(nombre: String?): GrupoMuscular =
            values().firstOrNull { it.name == nombre } ?: CORE

        private fun sinAcentos(s: String): String =
            Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}
