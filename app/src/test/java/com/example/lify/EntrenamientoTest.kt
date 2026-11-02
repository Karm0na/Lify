package com.example.lify

import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.alternarSerie
import com.example.lify.domain.anadirEjercicios
import com.example.lify.domain.anadirSerie
import com.example.lify.domain.aRealizados
import com.example.lify.domain.conPeso
import com.example.lify.domain.conReps
import com.example.lify.domain.crearEjercicioActivo
import com.example.lify.domain.descansoRestante
import com.example.lify.domain.quitarEjercicio
import com.example.lify.domain.quitarSerie
import com.example.lify.domain.saltarDescanso
import com.example.lify.domain.seriesCompletadas
import com.example.lify.domain.sumarDescanso
import com.example.lify.domain.volumenKg
import com.example.lify.model.SerieHecha
import com.example.lify.model.SesionActiva
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EntrenamientoTest {

    private fun sesion(
        series: Int = 3,
        pesoKg: Double = 0.0,
        reps: Int = 0,
        ref: List<SerieHecha> = emptyList(),
        unidad: UnidadPeso = UnidadPeso.KG,
        descanso: Int = 90
    ): SesionActiva {
        val ej = crearEjercicioActivo(1, "Press de banca", GrupoMuscular.PECHO, series, pesoKg, reps, ref, unidad)
        return SesionActiva(null, "Test", 0L, descanso, unidad.name, listOf(ej))
    }

    @Test
    fun crearEjercicioPrellenaConLaConfiguracionDeLaRutina() {
        val s = sesion(series = 4, pesoKg = 80.0, reps = 8)
        val e = s.ejercicios.single()
        assertEquals(4, e.series.size)
        assertEquals("80", e.series[0].peso)
        assertEquals("8", e.series[0].reps)
        assertTrue(e.series.none { it.completada })
    }

    @Test
    fun alMarcarUnaSerieArrancaElDescansoDeLaRutina() {
        val s = sesion(reps = 10, descanso = 90)
        val e = s.ejercicios.single()
        val (nueva, ok) = s.alternarSerie(e.clave, e.series[0].id, ahora = 1_000L)
        assertTrue(ok)
        assertEquals(1, nueva.seriesCompletadas())
        assertEquals(91_000L, nueva.descansoFin)
        assertEquals(90L, nueva.descansoRestante(1_000L))
        assertEquals(30L, nueva.descansoRestante(61_000L))
        assertEquals(0L, nueva.descansoRestante(200_000L))
    }

    @Test
    fun sinTemporizadorNoHayDescanso() {
        val s = sesion(reps = 10, descanso = 0)
        val e = s.ejercicios.single()
        val (nueva, _) = s.alternarSerie(e.clave, e.series[0].id, 5L)
        assertNull(nueva.descansoFin)
        assertNull(nueva.descansoRestante(5L))
    }

    @Test
    fun noSePuedeMarcarUnaSerieSinRepeticionesNiReferencia() {
        val s = sesion(reps = 0)
        val e = s.ejercicios.single()
        val (nueva, ok) = s.alternarSerie(e.clave, e.series[0].id, 0L)
        assertFalse(ok)
        assertEquals(0, nueva.seriesCompletadas())
    }

    @Test
    fun conReferenciaSeRellenanRepsYPesoVacios() {
        val s = sesion(reps = 0, ref = listOf(SerieHecha(60.0, 8)))
        val e = s.ejercicios.single()
        val (nueva, ok) = s.alternarSerie(e.clave, e.series[0].id, 0L)
        assertTrue(ok)
        val serie = nueva.ejercicios.single().series[0]
        assertEquals("8", serie.reps)
        assertEquals("60", serie.peso)
        assertTrue(serie.completada)
    }

    @Test
    fun desmarcarUnaSerieNoReiniciaElDescanso() {
        val s = sesion(reps = 10)
        val e = s.ejercicios.single()
        val (a, _) = s.alternarSerie(e.clave, e.series[0].id, 0L)
        val (b, ok) = a.alternarSerie(e.clave, e.series[0].id, 500L)
        assertTrue(ok)
        assertEquals(0, b.seriesCompletadas())
        assertEquals(a.descansoFin, b.descansoFin)
    }

    @Test
    fun guardarConvierteLibrasAKilosYDescartaSeriesSinMarcar() {
        val s0 = sesion(series = 2, unidad = UnidadPeso.LB)
        val e = s0.ejercicios.single()
        var s = s0.conPeso(e.clave, e.series[0].id, "220,5").conReps(e.clave, e.series[0].id, "5")
        s = s.conPeso(e.clave, e.series[1].id, "100").conReps(e.clave, e.series[1].id, "5")
        s = s.alternarSerie(e.clave, e.series[0].id, 0L).first // solo la primera
        val realizados = s.aRealizados()
        assertEquals(1, realizados.size)
        val serie = realizados[0].series.single()
        assertEquals(220.5 / 2.2046226218, serie.peso, 1e-6)
        assertEquals(5, serie.repeticiones)
    }

    @Test
    fun sinSeriesMarcadasNoHayNadaQueGuardar() {
        assertTrue(sesion().aRealizados().isEmpty())
    }

    @Test
    fun anadirSerieCopiaLaAnteriorYQuitarSerieYEjercicioFuncionan() {
        var s = sesion(series = 1, pesoKg = 50.0, reps = 10)
        val e = s.ejercicios.single()
        s = s.anadirSerie(e.clave)
        val e2 = s.ejercicios.single()
        assertEquals(2, e2.series.size)
        assertEquals("50", e2.series[1].peso)
        assertEquals("10", e2.series[1].reps)
        s = s.quitarSerie(e.clave, e2.series[0].id)
        assertEquals(1, s.ejercicios.single().series.size)
        s = s.quitarEjercicio(e.clave)
        assertTrue(s.ejercicios.isEmpty())
    }

    @Test
    fun anadirEjerciciosIgnoraLosYaPresentes() {
        val s = sesion()
        val repetido = crearEjercicioActivo(1, "Press de banca", GrupoMuscular.PECHO, 1, 0.0, 0, emptyList(), UnidadPeso.KG)
        val nuevo = crearEjercicioActivo(2, "Remo", GrupoMuscular.ESPALDA, 1, 0.0, 0, emptyList(), UnidadPeso.KG)
        val r = s.anadirEjercicios(listOf(repetido, nuevo))
        assertEquals(listOf(1, 2), r.ejercicios.map { it.ejercicioId })
    }

    @Test
    fun descansoSePuedeSumarYSaltar() {
        val s = sesion(reps = 10, descanso = 60)
        val e = s.ejercicios.single()
        val (a, _) = s.alternarSerie(e.clave, e.series[0].id, 0L)
        val b = a.sumarDescanso(10_000L, 15)
        assertEquals(60_000L + 15_000L, b.descansoFin)
        assertNull(b.saltarDescanso().descansoFin)
        // Si el descanso ya terminó, sumar parte de "ahora".
        val c = a.sumarDescanso(100_000L, 30)
        assertEquals(130_000L, c.descansoFin)
    }

    @Test
    fun elVolumenSoloCuentaLasSeriesMarcadasYSiempreEnKg() {
        // 3 series de 10 x 80 kg; solo marcamos dos: 2 x (10 x 80) = 1600 kg.
        var s = sesion(series = 3, pesoKg = 80.0, reps = 10)
        assertEquals(0.0, s.volumenKg(), 1e-9)
        val e = s.ejercicios.single()
        s = s.alternarSerie(e.clave, e.series[0].id, 0L).first
        s = s.alternarSerie(e.clave, e.series[1].id, 0L).first
        assertEquals(1600.0, s.volumenKg(), 1e-9)

        // Con libras (176.37 lb ≈ 80 kg) el volumen sigue saliendo en kg.
        var l = sesion(series = 1, pesoKg = 80.0, reps = 10, unidad = UnidadPeso.LB)
        val el = l.ejercicios.single()
        l = l.alternarSerie(el.clave, el.series[0].id, 0L).first
        assertEquals(800.0, l.volumenKg(), 1.0)
    }
}
