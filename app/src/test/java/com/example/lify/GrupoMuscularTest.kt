package com.example.lify

import com.example.lify.domain.GrupoMuscular
import org.junit.Assert.assertEquals
import org.junit.Test

class GrupoMuscularTest {

    @Test
    fun etiquetasOriginalesDelCatalogoSeNormalizan() {
        val esperado = mapOf(
            "Pectorales" to GrupoMuscular.PECHO,
            "Tríceps y pecho" to GrupoMuscular.TRICEPS,
            "Bíceps y espalda" to GrupoMuscular.BICEPS,
            "Bíceps y braquial" to GrupoMuscular.BICEPS,
            "Bíceps y antebrazos" to GrupoMuscular.BICEPS,
            "Abdominales" to GrupoMuscular.CORE,
            "Abdominales inferiores" to GrupoMuscular.CORE,
            "Abdominales y oblicuos" to GrupoMuscular.CORE,
            "Core" to GrupoMuscular.CORE,
            "Core y cardio" to GrupoMuscular.CORE,
            "Core y oblicuos" to GrupoMuscular.CORE,
            "Core y cuerpo completo" to GrupoMuscular.CORE,
            "Core y hombros" to GrupoMuscular.CORE,
            "Cardio" to GrupoMuscular.CARDIO,
            "Cardio y coordinación" to GrupoMuscular.CARDIO,
            "Cardio y cuerpo completo" to GrupoMuscular.CARDIO,
            "Cardio y fuerza" to GrupoMuscular.CARDIO,
            "Cardio y piernas" to GrupoMuscular.CARDIO,
            "Cuádriceps" to GrupoMuscular.CUADRICEPS,
            "Cuádriceps y glúteos" to GrupoMuscular.CUADRICEPS,
            "Espalda" to GrupoMuscular.ESPALDA,
            "Espalda baja" to GrupoMuscular.ESPALDA,
            "Espalda baja y core" to GrupoMuscular.ESPALDA,
            "Espalda baja y piernas" to GrupoMuscular.ESPALDA,
            "Espalda y cardio" to GrupoMuscular.ESPALDA,
            "Espalda y hombros" to GrupoMuscular.ESPALDA,
            "Gemelos" to GrupoMuscular.GEMELOS,
            "Glúteos" to GrupoMuscular.GLUTEOS,
            "Glúteos y isquiotibiales" to GrupoMuscular.GLUTEOS,
            "Glúteos y piernas" to GrupoMuscular.GLUTEOS,
            "Glúteos, piernas y core" to GrupoMuscular.GLUTEOS,
            "Hombros" to GrupoMuscular.HOMBROS,
            "Hombros posteriores" to GrupoMuscular.HOMBROS,
            "Hombros y cuerpo completo" to GrupoMuscular.HOMBROS,
            "Hombros y trapecios" to GrupoMuscular.HOMBROS,
            "Isquiotibiales" to GrupoMuscular.ISQUIOTIBIALES,
            "Isquiotibiales y glúteos" to GrupoMuscular.ISQUIOTIBIALES,
            "Piernas" to GrupoMuscular.PIERNAS,
            "Piernas y cardio" to GrupoMuscular.PIERNAS,
            "Piernas y cuerpo completo" to GrupoMuscular.PIERNAS,
            "Piernas y glúteos" to GrupoMuscular.PIERNAS,
            "Piernas y hombros" to GrupoMuscular.PIERNAS,
            "Trapecios" to GrupoMuscular.TRAPECIOS,
            "Tríceps" to GrupoMuscular.TRICEPS,
            "Bíceps" to GrupoMuscular.BICEPS
        )
        for ((etiqueta, grupo) in esperado) {
            assertEquals("Etiqueta: $etiqueta", grupo, GrupoMuscular.desdeEtiqueta(etiqueta))
        }
    }

    @Test
    fun lasEtiquetasDelCatalogoNormalizadoSonEstables() {
        for (g in GrupoMuscular.values()) {
            assertEquals(g, GrupoMuscular.desdeEtiqueta(g.etiquetaCatalogo))
        }
    }

    @Test
    fun nombreDesconocidoDevuelveCore() {
        assertEquals(GrupoMuscular.CORE, GrupoMuscular.desdeNombre("NO_EXISTE"))
        assertEquals(GrupoMuscular.PECHO, GrupoMuscular.desdeNombre("PECHO"))
    }
}
