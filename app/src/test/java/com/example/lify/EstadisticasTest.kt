package com.example.lify

import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.GrupoVolumen
import com.example.lify.domain.Periodo
import com.example.lify.domain.SerieHistorial
import com.example.lify.domain.diasEntrenadosSemana
import com.example.lify.domain.distribucionPorGrupo
import com.example.lify.domain.intensidadMusculos
import com.example.lify.domain.progresoPorSesion
import com.example.lify.domain.rachaSemanas
import com.example.lify.domain.unRmEstimado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Test

class EstadisticasTest {
    private val zona = ZoneId.of("Europe/Madrid")

    private fun ms(fecha: LocalDate): Long = fecha.atTime(10, 0).atZone(zona).toInstant().toEpochMilli()

    // Miércoles 28 de octubre de 2026 (la semana empieza el lunes 26).
    private val hoy = LocalDate.of(2026, 10, 28)

    @Test
    fun diasEntrenadosMarcaLaSemanaActualDeLunesADomingo() {
        val inicios = listOf(
            ms(LocalDate.of(2026, 10, 26)), // lunes
            ms(LocalDate.of(2026, 10, 28)), // miércoles
            ms(LocalDate.of(2026, 10, 25))  // domingo anterior: no cuenta
        )
        val dias = diasEntrenadosSemana(inicios, hoy, zona)
        assertEquals(listOf(true, false, true, false, false, false, false), dias)
    }

    @Test
    fun rachaCuentaSemanasConsecutivas() {
        val inicios = listOf(
            ms(LocalDate.of(2026, 10, 27)), // esta semana
            ms(LocalDate.of(2026, 10, 20)), // semana anterior
            ms(LocalDate.of(2026, 10, 14)), // hace dos semanas
            ms(LocalDate.of(2026, 9, 30))   // hueco: rompe la racha
        )
        assertEquals(3, rachaSemanas(inicios, hoy, zona))
    }

    @Test
    fun rachaNoSeRompeSiEstaSemanaAunNoSeHaEntrenado() {
        val inicios = listOf(ms(LocalDate.of(2026, 10, 20)), ms(LocalDate.of(2026, 10, 13)))
        assertEquals(2, rachaSemanas(inicios, hoy, zona))
    }

    @Test
    fun rachaEsCeroSiSeSaltoUnaSemanaCompleta() {
        val inicios = listOf(ms(LocalDate.of(2026, 10, 13)))
        assertEquals(0, rachaSemanas(inicios, hoy, zona))
        assertEquals(0, rachaSemanas(emptyList(), hoy, zona))
    }

    @Test
    fun periodosCalculanSuInicio() {
        assertEquals(LocalDate.of(2026, 10, 22), Periodo.SIETE_DIAS.inicio(hoy))
        assertEquals(LocalDate.of(2026, 9, 29), Periodo.TREINTA_DIAS.inicio(hoy))
        assertEquals(LocalDate.of(2026, 4, 28), Periodo.SEIS_MESES.inicio(hoy))
        assertEquals(LocalDate.of(2025, 10, 28), Periodo.UN_ANIO.inicio(hoy))
        assertEquals(null, Periodo.TODO.inicio(hoy))
        assertEquals(0L, Periodo.TODO.desdeMillis(hoy, zona))
        assertTrue(Periodo.SIETE_DIAS.desdeMillis(hoy, zona) > 0L)
    }

    @Test
    fun distribucionSumaCienYSeOrdena() {
        val datos = listOf(
            GrupoVolumen(GrupoMuscular.PECHO, 6, 1000.0),
            GrupoVolumen(GrupoMuscular.ESPALDA, 12, 2000.0),
            GrupoVolumen(GrupoMuscular.BICEPS, 2, 100.0)
        )
        val dist = distribucionPorGrupo(datos)
        assertEquals(GrupoMuscular.ESPALDA, dist[0].grupo)
        assertEquals(60f, dist[0].porcentaje, 0.001f)
        assertEquals(100f, dist.sumOf { it.porcentaje.toDouble() }.toFloat(), 0.01f)
        assertTrue(distribucionPorGrupo(emptyList()).isEmpty())
    }

    @Test
    fun intensidadEsRelativaAlGrupoMasTrabajado() {
        val dist = distribucionPorGrupo(
            listOf(GrupoVolumen(GrupoMuscular.PECHO, 10, 0.0), GrupoVolumen(GrupoMuscular.CORE, 5, 0.0))
        )
        val inten = intensidadMusculos(dist)
        assertEquals(1f, inten[GrupoMuscular.PECHO]!!, 1e-6f)
        assertEquals(0.5f, inten[GrupoMuscular.CORE]!!, 1e-6f)
        assertTrue(intensidadMusculos(emptyList()).isEmpty())
    }

    @Test
    fun unRmUsaEpley() {
        assertEquals(100.0, unRmEstimado(100.0, 1), 1e-9)
        assertEquals(133.333, unRmEstimado(100.0, 10), 0.001)
        assertEquals(0.0, unRmEstimado(100.0, 0), 1e-9)
        assertEquals(0.0, unRmEstimado(0.0, 10), 1e-9)
    }

    @Test
    fun progresoAgrupaPorSesionYOrdenaPorFecha() {
        val series = listOf(
            SerieHistorial(2, 2000, 60.0, 8),
            SerieHistorial(1, 1000, 50.0, 10),
            SerieHistorial(1, 1000, 55.0, 5),
            SerieHistorial(2, 2000, 62.5, 6)
        )
        val p = progresoPorSesion(series)
        assertEquals(2, p.size)
        assertEquals(1000L, p[0].fecha)
        assertEquals(55.0, p[0].pesoMaximo, 1e-9)
        assertEquals(50.0 * 10 + 55.0 * 5, p[0].volumen, 1e-9)
        assertEquals(62.5, p[1].pesoMaximo, 1e-9)
        assertEquals(60.0 * 8 + 62.5 * 6, p[1].volumen, 1e-9)
    }
}
