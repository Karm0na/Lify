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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        datos.forEach { d ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = d.etiqueta,
                    color = cs.onSurface,
                    fontSize = 13.sp,
                    modifier = Modifier.width(110.dp),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .background(cs.surfaceVariant, RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(d.fraccion.coerceIn(0.02f, 1f))
                            .height(12.dp)
                            .background(d.color, RoundedCornerShape(6.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = d.valor,
                    color = cs.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(44.dp)
                )
            }
        }
    }
}

/** Gráfica de línea sencilla con puntos. Debajo muestra el mínimo y el máximo del eje vertical. */
@Composable
fun GraficaLineas(valores: List<Double>, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val colorLinea = cs.primary
    val colorGuia = cs.outline
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (valores.isEmpty()) return@Canvas
            val relleno = 10.dp.toPx()
            val ancho = size.width - 2 * relleno
            val alto = size.height - 2 * relleno
            val minimo = valores.min()
            val maximo = valores.max()
            val rango = maximo - minimo

            fun xDe(i: Int): Float =
                if (valores.size == 1) size.width / 2f else relleno + ancho * i / (valores.size - 1).toFloat()

            fun yDe(v: Double): Float =
                if (rango < 1e-9) size.height / 2f else relleno + alto - (((v - minimo) / rango) * alto).toFloat()

            // Líneas guía arriba y abajo
            drawLine(colorGuia, Offset(relleno, relleno), Offset(size.width - relleno, relleno), strokeWidth = 1.dp.toPx())
            drawLine(colorGuia, Offset(relleno, size.height - relleno), Offset(size.width - relleno, size.height - relleno), strokeWidth = 1.dp.toPx())

            if (valores.size > 1) {
                val camino = Path()
                valores.forEachIndexed { i, v ->
                    if (i == 0) camino.moveTo(xDe(i), yDe(v)) else camino.lineTo(xDe(i), yDe(v))
                }
                drawPath(
                    camino,
                    colorLinea,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            valores.forEachIndexed { i, v ->
                drawCircle(colorLinea, radius = 4.5.dp.toPx(), center = Offset(xDe(i), yDe(v)))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatearNumero(valores.minOrNull() ?: 0.0),
                color = cs.onSurfaceVariant,
                fontSize = 11.sp
            )
            Text(
                text = formatearNumero(valores.maxOrNull() ?: 0.0),
                color = cs.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
