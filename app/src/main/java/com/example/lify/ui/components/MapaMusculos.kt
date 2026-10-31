package com.example.lify.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular

/** Una zona del cuerpo: elipse (centro y radios) en un lienzo de 100 x 210 unidades. */
private class Zona(val grupos: List<GrupoMuscular>, val cx: Float, val cy: Float, val rx: Float, val ry: Float)

private val ZONAS_FRENTE = listOf(
    Zona(listOf(GrupoMuscular.HOMBROS), 30f, 42f, 10f, 8f),
    Zona(listOf(GrupoMuscular.HOMBROS), 70f, 42f, 10f, 8f),
    Zona(listOf(GrupoMuscular.PECHO), 41f, 54f, 10f, 8f),
    Zona(listOf(GrupoMuscular.PECHO), 59f, 54f, 10f, 8f),
    Zona(listOf(GrupoMuscular.BICEPS), 20f, 62f, 5.5f, 12f),
    Zona(listOf(GrupoMuscular.BICEPS), 80f, 62f, 5.5f, 12f),
    Zona(listOf(GrupoMuscular.CORE), 50f, 82f, 9f, 19f),
    Zona(listOf(GrupoMuscular.CUADRICEPS, GrupoMuscular.PIERNAS), 41f, 128f, 8.5f, 25f),
    Zona(listOf(GrupoMuscular.CUADRICEPS, GrupoMuscular.PIERNAS), 59f, 128f, 8.5f, 25f),
    Zona(listOf(GrupoMuscular.GEMELOS), 40f, 176f, 6f, 17f),
    Zona(listOf(GrupoMuscular.GEMELOS), 60f, 176f, 6f, 17f)
)

private val ZONAS_ESPALDA = listOf(
    Zona(listOf(GrupoMuscular.HOMBROS), 30f, 42f, 10f, 8f),
    Zona(listOf(GrupoMuscular.HOMBROS), 70f, 42f, 10f, 8f),
    Zona(listOf(GrupoMuscular.TRAPECIOS), 50f, 40f, 12f, 8f),
    Zona(listOf(GrupoMuscular.ESPALDA), 39f, 62f, 9f, 14f),
    Zona(listOf(GrupoMuscular.ESPALDA), 61f, 62f, 9f, 14f),
    Zona(listOf(GrupoMuscular.ESPALDA), 50f, 88f, 9f, 9f),
    Zona(listOf(GrupoMuscular.TRICEPS), 20f, 62f, 5.5f, 12f),
    Zona(listOf(GrupoMuscular.TRICEPS), 80f, 62f, 5.5f, 12f),
    Zona(listOf(GrupoMuscular.GLUTEOS), 42f, 110f, 9f, 9f),
    Zona(listOf(GrupoMuscular.GLUTEOS), 58f, 110f, 9f, 9f),
    Zona(listOf(GrupoMuscular.ISQUIOTIBIALES, GrupoMuscular.PIERNAS), 41f, 138f, 8f, 20f),
    Zona(listOf(GrupoMuscular.ISQUIOTIBIALES, GrupoMuscular.PIERNAS), 59f, 138f, 8f, 20f),
    Zona(listOf(GrupoMuscular.GEMELOS), 40f, 176f, 6.5f, 17f),
    Zona(listOf(GrupoMuscular.GEMELOS), 60f, 176f, 6.5f, 17f)
)

private fun DrawScope.elipse(color: Color, ox: Float, oy: Float, s: Float, cx: Float, cy: Float, rx: Float, ry: Float) {
    drawOval(
        color = color,
        topLeft = Offset(ox + (cx - rx) * s, oy + (cy - ry) * s),
        size = Size(rx * 2 * s, ry * 2 * s)
    )
}

private fun DrawScope.figura(
    zonas: List<Zona>,
    intensidad: Map<GrupoMuscular, Float>,
    ox: Float,
    oy: Float,
    s: Float,
    neutro: Color,
    base: Color,
    acento: Color
) {
    // Silueta (partes que no se colorean)
    elipse(base, ox, oy, s, 50f, 16f, 11f, 12f)        // cabeza
    elipse(base, ox, oy, s, 50f, 31f, 5f, 4f)          // cuello
    elipse(base, ox, oy, s, 50f, 70f, 17f, 28f)        // tronco
    elipse(base, ox, oy, s, 16f, 86f, 4.5f, 12f)       // antebrazo izquierdo
    elipse(base, ox, oy, s, 84f, 86f, 4.5f, 12f)       // antebrazo derecho
    elipse(base, ox, oy, s, 50f, 104f, 16f, 9f)        // cadera
    // Zonas musculares
    for (z in zonas) {
        val valor = z.grupos.maxOf { intensidad[it] ?: 0f }
        val color = if (valor > 0f) lerp(neutro, acento, 0.3f + 0.7f * valor) else neutro
        elipse(color, ox, oy, s, z.cx, z.cy, z.rx, z.ry)
    }
}

/**
 * Silueta delantera y trasera con cada músculo coloreado según el volumen trabajado
 * (gris = nada, naranja intenso = lo más trabajado del periodo).
 */
@Composable
fun MapaMusculos(intensidad: Map<GrupoMuscular, Float>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val neutro = cs.surfaceVariant
    val base = cs.outline.copy(alpha = 0.55f)
    val acento = cs.primary
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val s = minOf(size.width / 230f, size.height / 210f)
            val anchoTotal = 230f * s
            val ox = (size.width - anchoTotal) / 2f
            val oy = (size.height - 210f * s) / 2f
            figura(ZONAS_FRENTE, intensidad, ox, oy, s, neutro, base, acento)
            figura(ZONAS_ESPALDA, intensidad, ox + 130f * s, oy, s, neutro, base, acento)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(stringResource(R.string.mapa_frente), color = cs.onSurfaceVariant, fontSize = 12.sp)
            Text(stringResource(R.string.mapa_espalda), color = cs.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}
