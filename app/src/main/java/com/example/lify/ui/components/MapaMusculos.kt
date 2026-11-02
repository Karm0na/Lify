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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular

/** Colores con los que se pinta el cuerpo (salen del tema, así funciona en claro y oscuro). */
private class PaletaCuerpo(
    val silueta: Color,
    val neutro: Color,
    val acento: Color,
    val separador: Color
)

@Composable
private fun paletaCuerpo(acento: Color = MaterialTheme.colorScheme.primary): PaletaCuerpo {
    val cs = MaterialTheme.colorScheme
    return PaletaCuerpo(
        silueta = cs.surfaceVariant,
        neutro = cs.outline,
        acento = acento,
        separador = cs.background.copy(alpha = 0.55f)
    )
}

/**
 * Dibuja una figura (frente o espalda) en el lienzo con origen en ([ox], [oy]) y escala [s].
 * Cada músculo se mezcla del gris neutro al color de acento según su intensidad (0..1) y los
 * trabajados llevan un halo tenue y un borde claro, como en las apps de referencia.
 */
private fun DrawScope.dibujarCuerpo(
    formas: List<FormaMuscular>,
    intensidad: Map<GrupoMuscular, Float>,
    ox: Float,
    oy: Float,
    s: Float,
    p: PaletaCuerpo
) {
    withTransform({
        translate(ox, oy)
        scale(s, s, Offset.Zero)
    }) {
        CuerpoFormas.silueta.forEach { drawPath(it, p.silueta) }

        // Primero los halos, para que nunca tapen el relleno de un músculo vecino.
        formas.forEach { f ->
            val v = f.grupos.maxOfOrNull { intensidad[it] ?: 0f } ?: 0f
            if (v > 0f) {
                drawPath(f.path, p.acento.copy(alpha = 0.10f + 0.10f * v), style = Stroke(width = 3.6f))
            }
        }
        formas.forEach { f ->
            val v = f.grupos.maxOfOrNull { intensidad[it] ?: 0f } ?: 0f
            if (v > 0f) {
                drawPath(f.path, lerp(p.neutro, p.acento, 0.3f + 0.7f * v))
                drawPath(f.path, lerp(p.acento, Color.White, 0.45f).copy(alpha = 0.55f), style = Stroke(width = 0.5f))
            } else {
                drawPath(f.path, p.neutro)
                drawPath(f.path, p.separador, style = Stroke(width = 0.5f))
            }
        }
    }
}

/** Frente y espalda lado a lado, centrados y ajustados al lienzo disponible. */
private fun DrawScope.dibujarFrenteYEspalda(intensidad: Map<GrupoMuscular, Float>, p: PaletaCuerpo) {
    val anchoVirtual = CUERPO_ANCHO * 2 + 30f
    val s = minOf(size.width / anchoVirtual, size.height / CUERPO_ALTO)
    val ox = (size.width - anchoVirtual * s) / 2f
    val oy = (size.height - CUERPO_ALTO * s) / 2f
    dibujarCuerpo(CuerpoFormas.frente, intensidad, ox, oy, s, p)
    dibujarCuerpo(CuerpoFormas.espalda, intensidad, ox + (CUERPO_ANCHO + 30f) * s, oy, s, p)
}

/**
 * Silueta delantera y trasera con cada músculo coloreado según el volumen trabajado
 * (gris = nada, ámbar intenso = lo más trabajado del periodo).
 */
@Composable
fun MapaMusculos(intensidad: Map<GrupoMuscular, Float>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val paleta = paletaCuerpo()
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            dibujarFrenteYEspalda(intensidad, paleta)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(stringResource(R.string.mapa_frente), color = cs.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.mapa_espalda), color = cs.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Versión compacta (sin etiquetas) para tarjetas: resalta por igual todos los [grupos] indicados.
 * Conviene darle una proporción de unos 230:210, por ejemplo con `Modifier.height(90.dp)` y un ancho fijo.
 */
@Composable
fun MiniMapaMusculos(grupos: Collection<GrupoMuscular>, modifier: Modifier = Modifier) {
    val paleta = paletaCuerpo()
    val intensidad = grupos.associateWith { 1f }
    Canvas(modifier = modifier) {
        dibujarFrenteYEspalda(intensidad, paleta)
    }
}

/** Zona del cuerpo que enseña el icono de un grupo: centro (en unidades del cuerpo) y lado del encuadre. */
private class Encuadre(val cx: Float, val cy: Float, val lado: Float)

private fun encuadreDe(grupo: GrupoMuscular): Encuadre = when (grupo) {
    GrupoMuscular.PECHO -> Encuadre(50f, 52f, 60f)
    GrupoMuscular.ESPALDA -> Encuadre(50f, 68f, 66f)
    GrupoMuscular.HOMBROS -> Encuadre(50f, 48f, 70f)
    GrupoMuscular.BICEPS -> Encuadre(50f, 58f, 78f)
    GrupoMuscular.TRICEPS -> Encuadre(50f, 58f, 78f)
    GrupoMuscular.TRAPECIOS -> Encuadre(50f, 44f, 58f)
    GrupoMuscular.CORE -> Encuadre(50f, 78f, 56f)
    GrupoMuscular.CUADRICEPS -> Encuadre(50f, 134f, 66f)
    GrupoMuscular.ISQUIOTIBIALES -> Encuadre(50f, 140f, 66f)
    GrupoMuscular.GLUTEOS -> Encuadre(50f, 112f, 60f)
    GrupoMuscular.GEMELOS -> Encuadre(50f, 176f, 56f)
    GrupoMuscular.PIERNAS -> Encuadre(50f, 150f, 110f)
    GrupoMuscular.CARDIO -> Encuadre(50f, 105f, 210f)
}

/**
 * Contenido del icono circular de un ejercicio: el cuerpo, ampliado a la zona del grupo y con ese
 * músculo iluminado en el color del grupo (frente o espalda, según donde se vea mejor).
 */
@Composable
fun IconoCuerpoGrupo(grupo: GrupoMuscular, colorGrupo: Color, modifier: Modifier = Modifier) {
    val paleta = paletaCuerpo(acento = colorGrupo)
    val encuadre = encuadreDe(grupo)
    val formas = if (grupo in CuerpoFormas.gruposDeEspalda) CuerpoFormas.espalda else CuerpoFormas.frente
    val intensidad: Map<GrupoMuscular, Float> = when (grupo) {
        GrupoMuscular.PIERNAS -> mapOf(GrupoMuscular.PIERNAS to 1f, GrupoMuscular.GEMELOS to 1f)
        GrupoMuscular.CARDIO -> GrupoMuscular.values().associateWith { 0.45f }
        else -> mapOf(grupo to 1f)
    }
    Canvas(modifier = modifier) {
        val s = size.minDimension / encuadre.lado
        val ox = size.width / 2f - encuadre.cx * s
        val oy = size.height / 2f - encuadre.cy * s
        dibujarCuerpo(formas, intensidad, ox, oy, s, paleta)
    }
}
