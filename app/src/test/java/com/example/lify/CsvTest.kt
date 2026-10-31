package com.example.lify

import com.example.lify.domain.FilaCsv
import com.example.lify.domain.generarCsv
import org.junit.Assert.assertEquals
import org.junit.Test

class CsvTest {
    @Test
    fun generaCabeceraYFilasConEscapado() {
        val csv = generarCsv(
            listOf(
                FilaCsv("2026-10-28", "Pecho, tríceps", "Press \"plano\"", "PECHO", 1, 80.0, 8),
                FilaCsv("2026-10-28", "Pecho, tríceps", "Press \"plano\"", "PECHO", 2, 82.5, 6)
            )
        )
        val lineas = csv.trimEnd().split("\n")
        assertEquals("fecha,entrenamiento,ejercicio,grupo,serie,peso_kg,repeticiones", lineas[0])
        assertEquals("2026-10-28,\"Pecho, tríceps\",\"Press \"\"plano\"\"\",PECHO,1,80.00,8", lineas[1])
        assertEquals("2026-10-28,\"Pecho, tríceps\",\"Press \"\"plano\"\"\",PECHO,2,82.50,6", lineas[2])
    }

    @Test
    fun sinFilasSoloHayCabecera() {
        assertEquals(1, generarCsv(emptyList()).trimEnd().split("\n").size)
    }
}
