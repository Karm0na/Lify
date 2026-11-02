package com.example.lify.ui.components

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.domain.GrupoMuscular
import com.example.lify.ui.etiquetaRes
import com.example.lify.ui.theme.LifyAmbarClaro
import com.example.lify.ui.theme.LifyAmbarOscuro

/** Radio de esquina estándar de las tarjetas. */
val RadioTarjeta = 20.dp

/** Degradado del acento (botones y elementos activos). */
@Composable
fun degradadoAcento(): Brush {
    val oscuro = MaterialTheme.colorScheme.background.red < 0.5f
    return if (oscuro) {
        Brush.linearGradient(listOf(LifyAmbarClaro, LifyAmbarOscuro))
    } else {
        Brush.linearGradient(listOf(LifyAmbarOscuro, LifyAmbarOscuro))
    }
}

/** Colores de los campos de texto con el estilo de Lify (relleno elevado, sin línea inferior). */
@Composable
fun colorCampo(): TextFieldColors {
    val cs = MaterialTheme.colorScheme
    return TextFieldDefaults.colors(
        focusedContainerColor = cs.surfaceVariant,
        unfocusedContainerColor = cs.surfaceVariant,
        cursorColor = cs.primary,
        focusedTextColor = cs.onSurface,
        unfocusedTextColor = cs.onSurface,
        focusedPlaceholderColor = cs.onSurfaceVariant,
        unfocusedPlaceholderColor = cs.onSurfaceVariant,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = cs.primary,
        unfocusedLeadingIconColor = cs.onSurfaceVariant
    )
}

/**
 * Tarjeta base: esquinas muy redondeadas, degradado sutil de arriba abajo y borde fino.
 * [resaltada] pinta el borde con el acento (para lo que está activo / seleccionado).
 */
@Composable
fun TarjetaLify(
    modifier: Modifier = Modifier,
    borde: Boolean = false,
    resaltada: Boolean = false,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(RadioTarjeta)
    val colorBorde = when {
        resaltada -> cs.primary.copy(alpha = 0.75f)
        borde -> cs.primary.copy(alpha = 0.35f)
        else -> cs.outline.copy(alpha = 0.7f)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, colorBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(
                Brush.verticalGradient(
                    listOf(cs.surfaceVariant.copy(alpha = 0.55f), cs.surface)
                )
            ),
            content = contenido
        )
    }
}

@Composable
fun TituloPantalla(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

/** Título de sección: más sobrio que antes (sin mayúsculas gritonas), con una barrita de acento. */
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

/** Botón principal: degradado del acento, esquinas redondeadas y un brillo suave debajo. */
@Composable
fun BotonPrimario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(16.dp)
    val fondo: Modifier = if (habilitado) {
        Modifier
            .shadow(10.dp, forma, ambientColor = cs.primary.copy(alpha = 0.35f), spotColor = cs.primary.copy(alpha = 0.45f))
            .background(degradadoAcento(), forma)
    } else {
        Modifier.background(cs.surfaceVariant, forma)
    }
    Box(
        modifier = modifier
            .height(56.dp)
            .then(fondo)
            .clip(forma)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleMedium,
            color = if (habilitado) cs.onPrimary else cs.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

/** Botón secundario: relleno elevado con borde fino. */
@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(forma)
            .background(cs.surfaceVariant.copy(alpha = 0.6f), forma)
            .border(1.dp, cs.outline, forma)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall,
            color = cs.onSurface
        )
    }
}

/** Selector tipo "píldora" (sustituye a los FilterChip): activa = degradado de acento. */
@Composable
fun PildoraLify(texto: String, seleccionada: Boolean, alPulsar: () -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .clip(forma)
            .then(
                if (seleccionada) Modifier.background(degradadoAcento(), forma)
                else Modifier
                    .background(cs.surfaceVariant.copy(alpha = 0.6f), forma)
                    .border(1.dp, cs.outline, forma)
            )
            .clickable(onClick = alPulsar)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionada) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (seleccionada) cs.onPrimary else cs.onSurface
        )
    }
}

/** Baldosa de dato: número grande de acento y etiqueta pequeña debajo (como las del resumen de las referencias). */
@Composable
fun BaldosaDato(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
    colorValor: Color = MaterialTheme.colorScheme.primary
) {
    TarjetaLify(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text(
                text = valor,
                color = colorValor,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Text(
                text = etiqueta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/** Barra de progreso fina con degradado de acento. [fraccion] entre 0 y 1. */
@Composable
fun BarraProgreso(fraccion: Float, modifier: Modifier = Modifier, alto: Dp = 8.dp) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(50))
            .background(cs.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraccion.coerceIn(0f, 1f))
                .height(alto)
                .clip(RoundedCornerShape(50))
                .background(degradadoAcento())
        )
    }
}

/**
 * Campo numérico en forma de baldosa (series, peso y repeticiones): fondo elevado, borde fino que
 * se enciende con el acento al enfocar y números centrados. Con [grande] los números son mucho más
 * grandes (para teclear con el gimnasio de fondo) y con [completada] la baldosa se tiñe de acento.
 * Los filtros de lo que se puede escribir los aplica el ViewModel, aquí solo se pinta.
 */
@Composable
fun CampoNumero(
    valor: String,
    alCambiar: (String) -> Unit,
    sugerencia: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
    grande: Boolean = false,
    completada: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val interaccion = remember { MutableInteractionSource() }
    val enfocado = interaccion.collectIsFocusedAsState().value
    val forma = RoundedCornerShape(if (grande) 16.dp else 12.dp)
    val tamanoTexto = if (grande) 22.sp else 18.sp
    val pesoTexto = if (grande) FontWeight.ExtraBold else FontWeight.Bold
    val colorTexto = if (completada) cs.primary else cs.onSurface
    BasicTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier,
        singleLine = true,
        interactionSource = interaccion,
        textStyle = TextStyle(
            color = colorTexto,
            fontSize = tamanoTexto,
            fontWeight = pesoTexto,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(cs.primary),
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        decorationBox = { campo ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (completada) cs.primary.copy(alpha = 0.10f) else cs.background.copy(alpha = 0.55f),
                        forma
                    )
                    .border(
                        width = if (enfocado) 1.5.dp else 1.dp,
                        color = when {
                            enfocado -> cs.primary
                            completada -> cs.primary.copy(alpha = 0.45f)
                            else -> cs.outline.copy(alpha = 0.8f)
                        },
                        shape = forma
                    )
                    .padding(horizontal = 4.dp, vertical = if (grande) 15.dp else 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (valor.isEmpty()) {
                    Text(
                        text = sugerencia,
                        color = cs.onSurfaceVariant.copy(alpha = 0.55f),
                        fontSize = tamanoTexto,
                        fontWeight = pesoTexto,
                        textAlign = TextAlign.Center
                    )
                }
                campo()
            }
        }
    )
}

/** Colores por grupo muscular, algo más sobrios/saturados para fondos oscuros. */
private val COLORES_GRUPO = listOf(
    Color(0xFFF87171), Color(0xFF60A5FA), Color(0xFFFBBF24), Color(0xFF4ADE80), Color(0xFFC084FC),
    Color(0xFF2DD4BF), Color(0xFFFB923C), Color(0xFF818CF8), Color(0xFFD6A77A), Color(0xFFF472B6),
    Color(0xFF94A3B8), Color(0xFFBEF264), Color(0xFF38BDF8)
)

/** Calcula un color consistente para cada grupo muscular */
fun colorDeGrupo(grupo: GrupoMuscular): Color = COLORES_GRUPO[grupo.ordinal % COLORES_GRUPO.size]

/**
 * Icono del ejercicio: círculo con el cuerpo humano ampliado a la zona trabajada y ese músculo
 * iluminado con el color del grupo. Si está [seleccionado] se convierte en un check de acento.
 */
@Composable
fun AvatarGrupo(grupo: GrupoMuscular, seleccionado: Boolean = false, tamano: Dp = 48.dp) {
    val color = colorDeGrupo(grupo)
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .then(
                if (seleccionado) Modifier.background(degradadoAcento(), CircleShape)
                else Modifier
                    .background(cs.surface, CircleShape)
                    .border(1.5.dp, color.copy(alpha = 0.6f), CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (seleccionado) {
            Icon(Icons.Default.Check, contentDescription = null, tint = cs.onPrimary, modifier = Modifier.size(tamano * 0.5f))
        } else {
            IconoCuerpoGrupo(grupo, color, Modifier.size(tamano))
        }
    }
}

@Composable
fun FilaEtiquetaValor(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
    }
}

/**
 * Cabecera de pantallas de edición/selección: botón circular de cerrar a la izquierda, título grande
 * en una sola línea y una píldora de acento a la derecha. Respeta la barra de estado.
 */
@Composable
fun CabeceraPantalla(
    titulo: String,
    descripcionCerrar: String,
    alCerrar: () -> Unit,
    textoAccion: String,
    alAccion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(cs.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(cs.surfaceVariant, CircleShape)
                .border(1.dp, cs.outline, CircleShape)
                .clickable(onClick = alCerrar),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, contentDescription = descripcionCerrar, tint = cs.onSurface, modifier = Modifier.size(20.dp))
        }
        Text(
            text = titulo,
            color = cs.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(degradadoAcento(), RoundedCornerShape(50))
                .clickable(onClick = alAccion)
        ) {
            Text(
                text = textoAccion,
                color = cs.onPrimary,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }
    }
}
