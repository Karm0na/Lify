package com.example.lify.data

import androidx.annotation.StringRes
import com.example.lify.R

/** Ejercicio de una rutina preconfigurada: id del catálogo + series y repeticiones. */
data class ItemPreconfigurado(val ejercicioId: Int, val series: Int, val repeticiones: Int)

data class RutinaPreconfigurada(
    @StringRes val titulo: Int,
    @StringRes val descripcion: Int,
    val descansoSegundos: Int,
    val ejercicios: List<ItemPreconfigurado>
)

/** Arnold Split (3 días) y otras rutinas clásicas: cuerpo completo, torso/pierna, empuje/tirón/pierna y sesiones enfocadas. Los ids son los de res/raw/ejercicios.json. */
val RUTINAS_PRECONFIGURADAS: List<RutinaPreconfigurada> = listOf(
    RutinaPreconfigurada(
        titulo = R.string.pre_pecho_espalda,
        descripcion = R.string.pre_pecho_espalda_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(1, 3, 10),   // Press de banca plano con barra
            ItemPreconfigurado(28, 3, 10),  // Jalón al pecho con polea
            ItemPreconfigurado(2, 3, 10)    // Press de banca inclinado con barra
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_brazos_hombro,
        descripcion = R.string.pre_brazos_hombro_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(62, 3, 10),  // Curl con mancuernas
            ItemPreconfigurado(81, 3, 10),  // Extensiones en polea con cuerda
            ItemPreconfigurado(43, 3, 10)   // Press Arnold
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_piernas,
        descripcion = R.string.pre_piernas_desc,
        descansoSegundos = 120,
        ejercicios = listOf(
            ItemPreconfigurado(88, 4, 8),   // Sentadillas con barra
            ItemPreconfigurado(91, 3, 10),  // Prensa de piernas
            ItemPreconfigurado(102, 3, 12), // Curl de piernas en máquina
            ItemPreconfigurado(107, 4, 15)  // Elevación de talones de pie
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_fullbody,
        descripcion = R.string.pre_fullbody_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(88, 3, 8),   // Sentadillas con barra
            ItemPreconfigurado(1, 3, 8),   // Press de banca plano con barra
            ItemPreconfigurado(24, 3, 8),   // Remo con barra
            ItemPreconfigurado(42, 3, 10),   // Press militar con mancuernas
            ItemPreconfigurado(99, 3, 10),   // Peso muerto rumano
            ItemPreconfigurado(111, 3, 15)   // Crunch en el suelo
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_torso,
        descripcion = R.string.pre_torso_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(1, 4, 8),   // Press de banca plano con barra
            ItemPreconfigurado(24, 4, 8),   // Remo con barra
            ItemPreconfigurado(42, 3, 10),   // Press militar con mancuernas
            ItemPreconfigurado(28, 3, 10),   // Jalón al pecho con polea
            ItemPreconfigurado(62, 3, 12),   // Curl con mancuernas
            ItemPreconfigurado(81, 3, 12)   // Extensiones en polea con cuerda
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_inferior,
        descripcion = R.string.pre_inferior_desc,
        descansoSegundos = 120,
        ejercicios = listOf(
            ItemPreconfigurado(39, 3, 5),   // Peso muerto convencional
            ItemPreconfigurado(88, 4, 8),   // Sentadillas con barra
            ItemPreconfigurado(90, 3, 10),   // Sentadillas búlgaras
            ItemPreconfigurado(102, 3, 12),   // Curl de piernas en máquina
            ItemPreconfigurado(100, 3, 12),   // Puente de glúteos
            ItemPreconfigurado(108, 4, 15)   // Elevación de talones sentado
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_empuje,
        descripcion = R.string.pre_empuje_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(1, 4, 8),   // Press de banca plano con barra
            ItemPreconfigurado(5, 3, 10),   // Press de banca inclinado con mancuernas
            ItemPreconfigurado(42, 3, 10),   // Press militar con mancuernas
            ItemPreconfigurado(44, 4, 15),   // Elevaciones laterales con mancuernas
            ItemPreconfigurado(81, 3, 12),   // Extensiones en polea con cuerda
            ItemPreconfigurado(77, 3, 10)   // Press francés con barra Z
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_tiron,
        descripcion = R.string.pre_tiron_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(21, 4, 8),   // Dominadas con agarre prono
            ItemPreconfigurado(24, 4, 8),   // Remo con barra
            ItemPreconfigurado(27, 3, 10),   // Remo con polea baja
            ItemPreconfigurado(37, 3, 15),   // Face Pulls
            ItemPreconfigurado(62, 3, 12),   // Curl con mancuernas
            ItemPreconfigurado(63, 3, 12)   // Curl tipo martillo
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_dia_pierna,
        descripcion = R.string.pre_dia_pierna_desc,
        descansoSegundos = 120,
        ejercicios = listOf(
            ItemPreconfigurado(88, 4, 8),   // Sentadillas con barra
            ItemPreconfigurado(91, 3, 12),   // Prensa de piernas
            ItemPreconfigurado(93, 3, 10),   // Zancadas con mancuernas
            ItemPreconfigurado(92, 3, 12),   // Extensiones de pierna en máquina
            ItemPreconfigurado(102, 3, 12),   // Curl de piernas en máquina
            ItemPreconfigurado(107, 4, 15)   // Elevación de talones de pie
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_pecho_triceps,
        descripcion = R.string.pre_pecho_triceps_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(1, 4, 8),   // Press de banca plano con barra
            ItemPreconfigurado(2, 3, 10),   // Press de banca inclinado con barra
            ItemPreconfigurado(7, 3, 12),   // Aperturas con mancuernas en banco plano
            ItemPreconfigurado(12, 3, 12),   // Crossover en poleas altas
            ItemPreconfigurado(81, 3, 12),   // Extensiones en polea con cuerda
            ItemPreconfigurado(77, 3, 10)   // Press francés con barra Z
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_espalda_biceps,
        descripcion = R.string.pre_espalda_biceps_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(21, 4, 8),   // Dominadas con agarre prono
            ItemPreconfigurado(24, 4, 8),   // Remo con barra
            ItemPreconfigurado(27, 3, 10),   // Remo con polea baja
            ItemPreconfigurado(28, 3, 10),   // Jalón al pecho con polea
            ItemPreconfigurado(61, 3, 10),   // Curl con barra
            ItemPreconfigurado(63, 3, 12)   // Curl tipo martillo
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_core,
        descripcion = R.string.pre_core_desc,
        descansoSegundos = 60,
        ejercicios = listOf(
            ItemPreconfigurado(111, 3, 15),   // Crunch en el suelo
            ItemPreconfigurado(118, 3, 12),   // Elevación de piernas colgado
            ItemPreconfigurado(120, 3, 20),   // Russian Twists
            ItemPreconfigurado(117, 3, 10),   // Ab Wheel Rollout (rueda abdominal)
            ItemPreconfigurado(121, 3, 15)   // V-Ups
        )
    ),
    RutinaPreconfigurada(
        titulo = R.string.pre_gluteos,
        descripcion = R.string.pre_gluteos_desc,
        descansoSegundos = 90,
        ejercicios = listOf(
            ItemPreconfigurado(98, 4, 10),   // Hip Thrust
            ItemPreconfigurado(99, 4, 10),   // Peso muerto rumano
            ItemPreconfigurado(100, 3, 12),   // Puente de glúteos
            ItemPreconfigurado(102, 3, 12),   // Curl de piernas en máquina
            ItemPreconfigurado(101, 3, 8),   // Peso muerto sumo
            ItemPreconfigurado(127, 3, 15)   // Kettlebell Swings
        )
    )
)
