package com.example.lify.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.domain.formatearNumero

/** Una barra horizontal: etiqueta, fracción 0..1 de la barra y texto de valor. */
data class BarraDato(val etiqueta: String, val fraccion: Float, val valor: String, val color: Color)

@Composable
fun GraficaBarras(datos: List<BarraDato>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        datos.forEach { d ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = d.etiqueta,
                        color = cs.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = d.valor,
                        color = cs.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(cs.surfaceVariant, RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(d.fraccion.coerceIn(0.01f, 1f))
                            .height(12.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(d.color.copy(alpha = 0.8f), d.color)),
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }
    }
}

/** Gráfica de línea con área rellena y puntos. Debajo muestra el mínimo y el máximo del eje vertical. Mejorada para mayor visibilidad y mejor gradiente. */
@Composable
fun GraficaLineas(valores: List<Double>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val colorLinea = cs.primary
    val colorRelleno = Brush.verticalGradient(
        colors = listOf(cs.primary.copy(alpha = 0.35f), Color.Transparent)
    )
    val colorGuia = cs.outline.copy(alpha = 0.35f)
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            if (valores.isEmpty()) return@Canvas
            val relleno = 16.dp.toPx()
            val ancho = size.width - 2 * relleno
            val alto = size.height - 2 * relleno
            val minimo = valores.min()
            val maximo = valores.max()
            val rango = if (maximo - minimo < 1e-9) 1.0 else maximo - minimo

            fun xDe(i: Int): Float =
                if (valores.size == 1) size.width / 2f else relleno + ancho * i / (valores.size - 1).toFloat()

            fun yDe(v: Double): Float =
                relleno + alto - (((v - minimo) / rango) * alto).toFloat()

            // Líneas guía horizontales - más visibles
            val pasos = 4
            for (i in 0..pasos) {
                val y = relleno + alto * i / pasos
                drawLine(colorGuia, Offset(relleno, y), Offset(size.width - relleno, y), strokeWidth = 1.5.dp.toPx())
            }

            if (valores.size > 1) {
                // Curva suave: cada tramo sale y llega en horizontal, así nunca se pasa de los datos.
                fun Path.trazarCurva() {
                    moveTo(xDe(0), yDe(valores[0]))
                    for (i in 1 until valores.size) {
                        val x0 = xDe(i - 1)
                        val x1 = xDe(i)
                        val medio = (x0 + x1) / 2f
                        cubicTo(medio, yDe(valores[i - 1]), medio, yDe(valores[i]), x1, yDe(valores[i]))
                    }
                }
                val camino = Path().apply { trazarCurva() }

                // Área rellena bajo la curva con gradiente mejorado
                val area = Path().apply {
                    trazarCurva()
                    lineTo(xDe(valores.size - 1), size.height - relleno)
                    lineTo(xDe(0), size.height - relleno)
                    close()
                }

                drawPath(area, colorRelleno)
                drawPath(
                    camino,
                    colorLinea,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            valores.forEachIndexed { i, v ->
                val ultimo = i == valores.lastIndex
                val centroPunto = Offset(xDe(i), yDe(v))
                if (ultimo) {
                    drawCircle(color = colorLinea.copy(alpha = 0.22f), radius = 12.dp.toPx(), center = centroPunto)
                }
                drawCircle(
                    color = if (ultimo) Color.White else colorLinea.copy(alpha = 0.55f),
                    radius = if (ultimo) 7.dp.toPx() else 4.dp.toPx(),
                    center = centroPunto
                )
                drawCircle(
                    color = colorLinea,
                    radius = if (ultimo) 4.5.dp.toPx() else 2.5.dp.toPx(),
                    center = centroPunto
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatearNumero(valores.minOrNull() ?: 0.0),
                color = cs.onSurface,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatearNumero(valores.maxOrNull() ?: 0.0),
                color = cs.onSurface,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Un eje del radar: etiqueta y valor relativo (0..1). */
class EjeRadar(val etiqueta: String, val valor: Float)

/**
 * Gráfico de araña (radar) para ver de un vistazo qué músculos se trabajan más y cuáles se
 * descuidan: anillos guía, polígono relleno con el acento y una etiqueta por eje.
 */
@Composable
fun GraficaRadar(ejes: List<EjeRadar>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val colorGuia = cs.outline.copy(alpha = 0.7f)
    val colorTexto = cs.onSurfaceVariant.toArgb()
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        val n = ejes.size
        if (n < 3) return@Canvas
        val margen = 64.dp.toPx()
        val radio = minOf(size.width, size.height) / 2f - margen
        val centro = Offset(size.width / 2f, size.height / 2f)

        fun punto(i: Int, fraccion: Float): Offset {
            val angulo = -PI / 2.0 + 2.0 * PI * i / n
            return Offset(
                centro.x + (cos(angulo) * radio * fraccion).toFloat(),
                centro.y + (sin(angulo) * radio * fraccion).toFloat()
            )
        }

        // Anillos guía (poligonales) y radios
        for (anillo in 1..4) {
            val f = anillo / 4f
            val poligono = Path().apply {
                for (i in 0 until n) {
                    val p = punto(i, f)
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(poligono, colorGuia, style = Stroke(width = 1.dp.toPx()))
        }
        for (i in 0 until n) {
            drawLine(colorGuia, centro, punto(i, 1f), strokeWidth = 1.dp.toPx())
        }

        // Polígono de datos
        val datos = Path().apply {
            ejes.forEachIndexed { i, e ->
                val p = punto(i, e.valor.coerceIn(0.04f, 1f))
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(datos, cs.primary.copy(alpha = 0.28f))
        drawPath(datos, cs.primary, style = Stroke(width = 2.5.dp.toPx(), join = StrokeJoin.Round))
        ejes.forEachIndexed { i, e ->
            if (e.valor > 0f) {
                drawCircle(cs.primary, radius = 3.5.dp.toPx(), center = punto(i, e.valor.coerceIn(0.04f, 1f)))
            }
        }

        // Etiquetas (se alinean hacia fuera según el lado en el que caen)
        val pincel = android.graphics.Paint().apply {
            isAntiAlias = true
            color = colorTexto
            textSize = 10.5.sp.toPx()
            isFakeBoldText = true
        }
        ejes.forEachIndexed { i, e ->
            val p = punto(i, 1.14f)
            pincel.textAlign = when {
                p.x < centro.x - 6.dp.toPx() -> android.graphics.Paint.Align.RIGHT
                p.x > centro.x + 6.dp.toPx() -> android.graphics.Paint.Align.LEFT
                else -> android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText(e.etiqueta, p.x, p.y + pincel.textSize / 3f, pincel)
        }
    }
}
