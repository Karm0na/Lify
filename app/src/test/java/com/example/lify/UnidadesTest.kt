package com.example.lify

import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.formatearDuracion
import com.example.lify.domain.formatearNumero
import com.example.lify.domain.parsearNumero
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UnidadesTest {

    @Test
    fun conversionKgLbEsReversible() {
        val kg = 100.0
        val lb = UnidadPeso.LB.desdeKg(kg)
        assertEquals(220.462, lb, 0.001)
        assertEquals(kg, UnidadPeso.LB.aKg(lb), 1e-9)
        assertEquals(kg, UnidadPeso.KG.desdeKg(kg), 1e-9)
    }

    @Test
    fun formatoNumeroQuitaCerosSobrantes() {
        assertEquals("80", formatearNumero(80.0))
        assertEquals("82.5", formatearNumero(82.5))
        assertEquals("82.5", formatearNumero(82.50001))
        assertEquals("0", formatearNumero(0.0))
        assertEquals("12.3", formatearNumero(12.34))
    }

    @Test
    fun parseaComaYPuntoYRechazaInvalidos() {
        assertEquals(82.5, parsearNumero("82,5")!!, 1e-9)
        assertEquals(82.5, parsearNumero(" 82.5 ")!!, 1e-9)
        assertNull(parsearNumero(""))
        assertNull(parsearNumero("abc"))
        assertNull(parsearNumero("-5"))
    }

    @Test
    fun duracionSeFormateaComoReloj() {
        assertEquals("00:05", formatearDuracion(5))
        assertEquals("01:30", formatearDuracion(90))
        assertEquals("1:01:01", formatearDuracion(3661))
        assertEquals("00:00", formatearDuracion(-3))
    }
}

class TextoTest {
    @Test
    fun busquedaIgnoraAcentosYMayusculas() {
        assertEquals("biceps", com.example.lify.domain.normalizarBusqueda("Bíceps"))
        assertEquals("cuadriceps y gluteos", com.example.lify.domain.normalizarBusqueda("Cuádriceps y Glúteos"))
    }

    @Test
    fun filtrosDeEntradaNumerica() {
        assertEquals("12", com.example.lify.domain.filtrarEntero("1a2b3", 2))
        assertEquals("82,5", com.example.lify.domain.filtrarDecimal("8x2,5"))
        assertEquals("82,56", com.example.lify.domain.filtrarDecimal("82,5,6"))
        assertEquals("1234567", com.example.lify.domain.filtrarDecimal("123456789"))
    }
}
