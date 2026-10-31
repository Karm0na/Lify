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

/** Arnold Split: 3 días. Los ids son los de res/raw/ejercicios.json. */
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
    )
)
