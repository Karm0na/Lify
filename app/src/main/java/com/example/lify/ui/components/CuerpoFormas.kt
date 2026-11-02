package com.example.lify.ui.components

import androidx.compose.ui.graphics.Path
import com.example.lify.domain.GrupoMuscular

/**
 * Anatomía vectorial del cuerpo humano (vista frontal y trasera) en un lienzo de 100 x 210 unidades.
 *
 * Los trazados están en una mini-sintaxis tipo SVG (solo M, L, C, Q y Z, siempre en coordenadas
 * absolutas y con cada comando repetido) que [trazar] convierte en un [Path] de Compose. Cada
 * músculo es una forma independiente: así se puede colorear según el volumen trabajado y separar
 * del resto con un trazo fino, como en las apps de referencia.
 *
 * Archivo generado a partir de los diseños validados visualmente; si lo editas a mano, mantén la
 * simetría (la mitad derecha es la izquierda espejada respecto a x = 50).
 */
internal class FormaMuscular(val grupos: List<GrupoMuscular>, val path: Path)

internal const val CUERPO_ANCHO = 100f
internal const val CUERPO_ALTO = 210f

private fun trazar(d: String): Path {
    val camino = Path()
    val partes = d.split(' ')
    var i = 0
    fun n(): Float = partes[i++].toFloat()
    while (i < partes.size) {
        when (partes[i++]) {
            "M" -> camino.moveTo(n(), n())
            "L" -> camino.lineTo(n(), n())
            "C" -> camino.cubicTo(n(), n(), n(), n(), n(), n())
            "Q" -> camino.quadraticBezierTo(n(), n(), n(), n())
            "Z" -> camino.close()
        }
    }
    return camino
}

internal object CuerpoFormas {
    /** Cabeza, cuello, torso y extremidades (compartidos por la vista frontal y la trasera). */
    val silueta: List<Path> by lazy {
        listOf(
            trazar("M 50 22 L 45 23.5 C 45 28 44.5 30 41.5 32.5 C 35.5 34 29.5 35 25.5 39 C 21.5 42 19.5 48 19 55 C 18.5 64 17.5 72 16 80 C 14.5 88 12.5 96 10.5 104 C 9 108 7 112 7 116.5 C 7 121 9.5 123 11.5 120.5 C 12.5 115 14.5 109 16.5 103 C 18.5 95 20.5 87 22.5 79 C 24.5 71 26.5 64 28.5 59 C 29 68 31 80 32 90 C 32.5 98 31.5 104 33 112 C 31.5 122 30 136 30.5 150 C 30.5 162 29.5 173 30.5 186 C 30.5 192 30.5 198 31.5 203 C 28.5 205.5 26.5 207 26.5 208.5 L 40 208.5 C 40.5 203 40.5 196 40.5 188 C 40.5 175 41.5 165 42.5 152 C 43.5 140 45.5 125 49 115 L 50 112 Z"),
            trazar("M 50 22 L 55 23.5 C 55 28 55.5 30 58.5 32.5 C 64.5 34 70.5 35 74.5 39 C 78.5 42 80.5 48 81 55 C 81.5 64 82.5 72 84 80 C 85.5 88 87.5 96 89.5 104 C 91 108 93 112 93 116.5 C 93 121 90.5 123 88.5 120.5 C 87.5 115 85.5 109 83.5 103 C 81.5 95 79.5 87 77.5 79 C 75.5 71 73.5 64 71.5 59 C 71 68 69 80 68 90 C 67.5 98 68.5 104 67 112 C 68.5 122 70 136 69.5 150 C 69.5 162 70.5 173 69.5 186 C 69.5 192 69.5 198 68.5 203 C 71.5 205.5 73.5 207 73.5 208.5 L 60 208.5 C 59.5 203 59.5 196 59.5 188 C 59.5 175 58.5 165 57.5 152 C 56.5 140 54.5 125 51 115 L 50 112 Z"),
            trazar("M 41.4 13.5 C 41.4 7.64562 45.2502 2.9 50 2.9 C 54.7498 2.9 58.6 7.64562 58.6 13.5 C 58.6 19.3544 54.7498 24.1 50 24.1 C 45.2502 24.1 41.4 19.3544 41.4 13.5 Z"),
            trazar("M 45.2 23 L 54.8 23 L 55.6 32 C 53 33.5 47 33.5 44.4 32 Z")
        )
    }

    val frente: List<FormaMuscular> by lazy {
        listOf(
        FormaMuscular(listOf(GrupoMuscular.TRAPECIOS), trazar("M 44.8 27 C 44.4 32 43.4 36 41.2 39.8 C 38 38.2 34.8 37.6 32.6 37.4 C 37 35.4 41 33 44.8 27 Z")),  // trap
        FormaMuscular(listOf(GrupoMuscular.TRAPECIOS), trazar("M 55.2 27 C 55.6 32 56.6 36 58.8 39.8 C 62 38.2 65.2 37.6 67.4 37.4 C 63 35.4 59 33 55.2 27 Z")),  // trap_r
        FormaMuscular(listOf(GrupoMuscular.HOMBROS), trazar("M 26 39.3 C 30 36.2 34.4 36.6 36.6 41.5 C 37.8 46.5 35.6 52 31.2 54.2 C 26.4 56.2 21.2 56 19.6 52.5 C 19.4 47.2 21.6 42 26 39.3 Z")),  // delt
        FormaMuscular(listOf(GrupoMuscular.HOMBROS), trazar("M 74 39.3 C 70 36.2 65.6 36.6 63.4 41.5 C 62.2 46.5 64.4 52 68.8 54.2 C 73.6 56.2 78.8 56 80.4 52.5 C 80.6 47.2 78.4 42 74 39.3 Z")),  // delt_r
        FormaMuscular(listOf(GrupoMuscular.PECHO), trazar("M 49.3 41.6 C 45 39.8 40.4 40 37.4 43.2 C 35.8 48 36.4 54 39.4 58 C 43.4 60.4 47 59.6 49.3 58.2 Z")),  // pec
        FormaMuscular(listOf(GrupoMuscular.PECHO), trazar("M 50.7 41.6 C 55 39.8 59.6 40 62.6 43.2 C 64.2 48 63.6 54 60.6 58 C 56.6 60.4 53 59.6 50.7 58.2 Z")),  // pec_r
        FormaMuscular(listOf(GrupoMuscular.BICEPS), trazar("M 19.8 55.6 C 22.6 58.2 27 58.2 30.2 56 C 31 63 29.6 70 27.4 76.4 C 24.4 78.6 21.2 78 18.8 75.4 C 18.2 68.4 18.6 61.4 19.8 55.6 Z")),  // bic
        FormaMuscular(listOf(GrupoMuscular.BICEPS), trazar("M 80.2 55.6 C 77.4 58.2 73 58.2 69.8 56 C 69 63 70.4 70 72.6 76.4 C 75.6 78.6 78.8 78 81.2 75.4 C 81.8 68.4 81.4 61.4 80.2 55.6 Z")),  // bic_r
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 42.6 63.4 C 40.4 63 38.2 62.4 36.4 61 C 35.6 70.4 36 82 37 92 C 38.8 94.6 40.8 96 42.6 96.6 Z")),  // obl
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 57.4 63.4 C 59.6 63 61.8 62.4 63.6 61 C 64.4 70.4 64 82 63 92 C 61.2 94.6 59.2 96 57.4 96.6 Z")),  // obl_r
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 45.4 61.5 L 47.7 61.5 Q 49.3 61.5 49.3 63.1 L 49.3 67.2 Q 49.3 68.8 47.7 68.8 L 45.4 68.8 Q 43.8 68.8 43.8 67.2 L 43.8 63.1 Q 43.8 61.5 45.4 61.5 Z")),  // abs0
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 54.6 61.5 L 52.3 61.5 Q 50.7 61.5 50.7 63.1 L 50.7 67.2 Q 50.7 68.8 52.3 68.8 L 54.6 68.8 Q 56.2 68.8 56.2 67.2 L 56.2 63.1 Q 56.2 61.5 54.6 61.5 Z")),  // abs0_r
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 45.4 70.6 L 47.7 70.6 Q 49.3 70.6 49.3 72.2 L 49.3 76 Q 49.3 77.6 47.7 77.6 L 45.4 77.6 Q 43.8 77.6 43.8 76 L 43.8 72.2 Q 43.8 70.6 45.4 70.6 Z")),  // abs1
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 54.6 70.6 L 52.3 70.6 Q 50.7 70.6 50.7 72.2 L 50.7 76 Q 50.7 77.6 52.3 77.6 L 54.6 77.6 Q 56.2 77.6 56.2 76 L 56.2 72.2 Q 56.2 70.6 54.6 70.6 Z")),  // abs1_r
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 45.4 79.4 L 47.7 79.4 Q 49.3 79.4 49.3 81 L 49.3 85.6 Q 49.3 87.2 47.7 87.2 L 45.4 87.2 Q 43.8 87.2 43.8 85.6 L 43.8 81 Q 43.8 79.4 45.4 79.4 Z")),  // abs2
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 54.6 79.4 L 52.3 79.4 Q 50.7 79.4 50.7 81 L 50.7 85.6 Q 50.7 87.2 52.3 87.2 L 54.6 87.2 Q 56.2 87.2 56.2 85.6 L 56.2 81 Q 56.2 79.4 54.6 79.4 Z")),  // abs2_r
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 45.4 89 L 47.7 89 Q 49.3 89 49.3 90.6 L 49.3 96 Q 49.3 97.6 47.7 97.6 L 45.4 97.6 Q 43.8 97.6 43.8 96 L 43.8 90.6 Q 43.8 89 45.4 89 Z")),  // abs3
        FormaMuscular(listOf(GrupoMuscular.CORE), trazar("M 54.6 89 L 52.3 89 Q 50.7 89 50.7 90.6 L 50.7 96 Q 50.7 97.6 52.3 97.6 L 54.6 97.6 Q 56.2 97.6 56.2 96 L 56.2 90.6 Q 56.2 89 54.6 89 Z")),  // abs3_r
        FormaMuscular(listOf(GrupoMuscular.CUADRICEPS, GrupoMuscular.PIERNAS), trazar("M 33.4 114 C 37 112.4 41 114.2 43.8 118 C 45.6 129 45 142 42.4 152.4 C 39.2 155 35 155 32 152.4 C 30.4 140 31.2 126 33.4 114 Z")),  // quad
        FormaMuscular(listOf(GrupoMuscular.CUADRICEPS, GrupoMuscular.PIERNAS), trazar("M 66.6 114 C 63 112.4 59 114.2 56.2 118 C 54.4 129 55 142 57.6 152.4 C 60.8 155 65 155 68 152.4 C 69.6 140 68.8 126 66.6 114 Z")),  // quad_r
        FormaMuscular(listOf(GrupoMuscular.GEMELOS), trazar("M 32.2 160.4 C 35 158.4 39 160 40.2 164 C 40.6 175 39.6 186 38 193 C 35.2 194 32.6 193.2 31.6 191 C 30.8 182 30.6 170 32.2 160.4 Z")),  // calf
        FormaMuscular(listOf(GrupoMuscular.GEMELOS), trazar("M 67.8 160.4 C 65 158.4 61 160 59.8 164 C 59.4 175 60.4 186 62 193 C 64.8 194 67.4 193.2 68.4 191 C 69.2 182 69.4 170 67.8 160.4 Z")),  // calf_r
        FormaMuscular(emptyList(), trazar("M 16.2 79.4 C 18.8 80.8 21.8 80.4 23.4 78.6 C 22 87 19.6 95 17.2 102.6 C 15 103.2 12.8 103 11 102.2 C 12.8 94.6 14.6 87 16.2 79.4 Z")),  // fore
        FormaMuscular(emptyList(), trazar("M 83.8 79.4 C 81.2 80.8 78.2 80.4 76.6 78.6 C 78 87 80.4 95 82.8 102.6 C 85 103.2 87.2 103 89 102.2 C 87.2 94.6 85.4 87 83.8 79.4 Z")),  // fore_r
        )
    }

    val espalda: List<FormaMuscular> by lazy {
        listOf(
        FormaMuscular(listOf(GrupoMuscular.TRAPECIOS), trazar("M 50 25 L 44.8 27 C 41.2 31 36.4 35 31.4 38.6 C 37 41 42 46 44 52 C 46 58 48.6 63 50 68.5 Z")),  // trap
        FormaMuscular(listOf(GrupoMuscular.TRAPECIOS), trazar("M 50 25 L 55.2 27 C 58.8 31 63.6 35 68.6 38.6 C 63 41 58 46 56 52 C 54 58 51.4 63 50 68.5 Z")),  // trap_r
        FormaMuscular(listOf(GrupoMuscular.HOMBROS), trazar("M 26 39.3 C 30 36.2 34.4 36.6 36.6 41.5 C 37.8 46.5 35.6 52 31.2 54.2 C 26.4 56.2 21.2 56 19.6 52.5 C 19.4 47.2 21.6 42 26 39.3 Z")),  // delt
        FormaMuscular(listOf(GrupoMuscular.HOMBROS), trazar("M 74 39.3 C 70 36.2 65.6 36.6 63.4 41.5 C 62.2 46.5 64.4 52 68.8 54.2 C 73.6 56.2 78.8 56 80.4 52.5 C 80.6 47.2 78.4 42 74 39.3 Z")),  // delt_r
        FormaMuscular(listOf(GrupoMuscular.ESPALDA), trazar("M 29.8 58.6 C 34 56.2 39 55.2 44.2 56.4 C 45.2 64 46.4 72 47.4 80 C 42.8 84 37.2 86 33.8 84.4 C 32.8 75.4 31.4 66.4 29.8 58.6 Z")),  // lat
        FormaMuscular(listOf(GrupoMuscular.ESPALDA), trazar("M 70.2 58.6 C 66 56.2 61 55.2 55.8 56.4 C 54.8 64 53.6 72 52.6 80 C 57.2 84 62.8 86 66.2 84.4 C 67.2 75.4 68.6 66.4 70.2 58.6 Z")),  // lat_r
        FormaMuscular(listOf(GrupoMuscular.ESPALDA), trazar("M 47.4 83 L 49.4 83 L 49.4 103 C 46.2 102.8 43.6 101 42 98.4 C 43 92.4 45 87.2 47.4 83 Z")),  // lumb
        FormaMuscular(listOf(GrupoMuscular.ESPALDA), trazar("M 52.6 83 L 50.6 83 L 50.6 103 C 53.8 102.8 56.4 101 58 98.4 C 57 92.4 55 87.2 52.6 83 Z")),  // lumb_r
        FormaMuscular(emptyList(), trazar("M 16.2 79.4 C 18.8 80.8 21.8 80.4 23.4 78.6 C 22 87 19.6 95 17.2 102.6 C 15 103.2 12.8 103 11 102.2 C 12.8 94.6 14.6 87 16.2 79.4 Z")),  // fore
        FormaMuscular(emptyList(), trazar("M 83.8 79.4 C 81.2 80.8 78.2 80.4 76.6 78.6 C 78 87 80.4 95 82.8 102.6 C 85 103.2 87.2 103 89 102.2 C 87.2 94.6 85.4 87 83.8 79.4 Z")),  // fore_r
        FormaMuscular(listOf(GrupoMuscular.TRICEPS), trazar("M 19.8 55.6 C 22.6 58.2 27 58.2 30.2 56 C 31 63 29.6 70 27.4 76.4 C 24.4 78.6 21.2 78 18.8 75.4 C 18.2 68.4 18.6 61.4 19.8 55.6 Z")),  // tri
        FormaMuscular(listOf(GrupoMuscular.TRICEPS), trazar("M 80.2 55.6 C 77.4 58.2 73 58.2 69.8 56 C 69 63 70.4 70 72.6 76.4 C 75.6 78.6 78.8 78 81.2 75.4 C 81.8 68.4 81.4 61.4 80.2 55.6 Z")),  // tri_r
        FormaMuscular(listOf(GrupoMuscular.GLUTEOS), trazar("M 49.4 101.4 C 44 99.4 38 100.8 34.4 104.6 C 32.6 109 33.2 115 36.4 118.6 C 40.4 121.4 46 121.4 49.4 119.6 Z")),  // glu
        FormaMuscular(listOf(GrupoMuscular.GLUTEOS), trazar("M 50.6 101.4 C 56 99.4 62 100.8 65.6 104.6 C 67.4 109 66.8 115 63.6 118.6 C 59.6 121.4 54 121.4 50.6 119.6 Z")),  // glu_r
        FormaMuscular(listOf(GrupoMuscular.ISQUIOTIBIALES, GrupoMuscular.PIERNAS), trazar("M 34.2 123 C 37.4 121.6 42 122.4 45 123.4 C 45.6 135 44.6 147 42.2 156 C 38.6 158.2 35 157.6 32.6 155.4 C 31.6 143 32.2 132 34.2 123 Z")),  // ham
        FormaMuscular(listOf(GrupoMuscular.ISQUIOTIBIALES, GrupoMuscular.PIERNAS), trazar("M 65.8 123 C 62.6 121.6 58 122.4 55 123.4 C 54.4 135 55.4 147 57.8 156 C 61.4 158.2 65 157.6 67.4 155.4 C 68.4 143 67.8 132 65.8 123 Z")),  // ham_r
        FormaMuscular(listOf(GrupoMuscular.GEMELOS), trazar("M 32.2 160.2 C 35 158.6 40 160 41.2 163.4 C 42.2 172 40.6 182 38.6 190 C 36 192.4 33 191.6 31.6 188.4 C 30.4 178 30.4 168 32.2 160.2 Z")),  // calf
        FormaMuscular(listOf(GrupoMuscular.GEMELOS), trazar("M 67.8 160.2 C 65 158.6 60 160 58.8 163.4 C 57.8 172 59.4 182 61.4 190 C 64 192.4 67 191.6 68.4 188.4 C 69.6 178 69.6 168 67.8 160.2 Z")),  // calf_r
        )
    }

    /** Grupos que se ven por detrás: sirven para elegir qué vista enseñar en los iconos pequeños. */
    val gruposDeEspalda = setOf(
        GrupoMuscular.ESPALDA,
        GrupoMuscular.TRAPECIOS,
        GrupoMuscular.TRICEPS,
        GrupoMuscular.GLUTEOS,
        GrupoMuscular.ISQUIOTIBIALES
    )
}
