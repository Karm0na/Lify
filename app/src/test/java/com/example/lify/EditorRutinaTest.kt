package com.example.lify

import com.example.lify.domain.EditorRutinaState
import com.example.lify.domain.EjercicioEditable
import com.example.lify.domain.ErrorEditor
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.OPCIONES_DESCANSO
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.aConfig
import com.example.lify.domain.aEditor
import com.example.lify.domain.mover
import com.example.lify.domain.validar
import com.example.lify.model.EjercicioEnRutina
import com.example.lify.model.Rutina
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorRutinaTest {

    private fun ej(clave: Long, id: Int, series: String = "3", peso: String = "", reps: String = "10") =
        EjercicioEditable(clave, id, "Ejercicio $id", GrupoMuscular.PECHO, series, peso, reps)

    @Test
    fun validaNombreYEjercicios() {
        assertEquals(ErrorEditor.SIN_NOMBRE, EditorRutinaState(nombre = "  ", ejercicios = listOf(ej(1, 1))).validar())
        assertEquals(ErrorEditor.SIN_EJERCICIOS, EditorRutinaState(nombre = "Pecho").validar())
        assertNull(EditorRutinaState(nombre = "Pecho", ejercicios = listOf(ej(1, 1))).validar())
    }

    @Test
    fun aConfigAcotaValoresYConvierteAKilos() {
        val estado = EditorRutinaState(
            nombre = "X",
            unidad = UnidadPeso.LB,
            ejercicios = listOf(
                ej(1, 10, series = "0", peso = "220,462", reps = "abc"),
                ej(2, 11, series = "99", peso = "", reps = "12")
            )
        )
        val c = estado.aConfig()
        assertEquals(1, c[0].series)            // mínimo 1
        assertEquals(100.0, c[0].peso, 0.001)   // 220.462 lb = 100 kg
        assertEquals(0, c[0].repeticiones)      // inválido = 0
        assertEquals(20, c[1].series)           // máximo 20
        assertEquals(0.0, c[1].peso, 1e-9)
        assertEquals(12, c[1].repeticiones)
    }

    @Test
    fun editarUnaRutinaGuardadaYVolverAGuardarConservaLosDatos() {
        val rutina = Rutina(
            7, "Pecho", 90,
            listOf(EjercicioEnRutina(1, "Press", GrupoMuscular.PECHO, 4, 82.5, 8))
        )
        val estado = rutina.aEditor(UnidadPeso.KG)
        assertEquals(7L, estado.rutinaId)
        assertEquals("82.5", estado.ejercicios[0].peso)
        val c = estado.aConfig().single()
        assertEquals(4, c.series)
        assertEquals(82.5, c.peso, 1e-9)
        assertEquals(8, c.repeticiones)
    }

    @Test
    fun moverReordenaYRespetaLosLimites() {
        val e = EditorRutinaState(nombre = "X", ejercicios = listOf(ej(1, 1), ej(2, 2), ej(3, 3)))
        assertEquals(listOf(2, 1, 3), e.mover(2, -1).ejercicios.map { it.ejercicioId })
        assertEquals(listOf(1, 3, 2), e.mover(2, 1).ejercicios.map { it.ejercicioId })
        assertEquals(listOf(1, 2, 3), e.mover(1, -1).ejercicios.map { it.ejercicioId })
        assertEquals(listOf(1, 2, 3), e.mover(3, 1).ejercicios.map { it.ejercicioId })
    }

    @Test
    fun descansoVaDe30sA5MinMasApagado() {
        assertEquals(0, OPCIONES_DESCANSO.first())
        assertEquals(30, OPCIONES_DESCANSO[1])
        assertEquals(300, OPCIONES_DESCANSO.last())
        assertTrue(OPCIONES_DESCANSO.zipWithNext().all { it.first < it.second })
    }
}
