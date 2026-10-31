package com.example.lify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/** Colores de los campos de texto con el estilo de Lify (fondo = superficie, sin línea inferior). */
@Composable
fun colorCampo(): TextFieldColors {
    val cs = MaterialTheme.colorScheme
    return TextFieldDefaults.colors(
        focusedContainerColor = cs.surface,
        unfocusedContainerColor = cs.surface,
        cursorColor = cs.primary,
        focusedTextColor = cs.onSurface,
        unfocusedTextColor = cs.onSurface,
        focusedPlaceholderColor = cs.onSurfaceVariant,
        unfocusedPlaceholderColor = cs.onSurfaceVariant,
        focusedIndicatorColor = cs.primary,
        unfocusedIndicatorColor = Color.Transparent
    )
}

@Composable
fun TarjetaLify(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        content = contenido
    )
}

@Composable
fun TituloPantalla(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun BotonPrimario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold)
    }
}

/**
 * Campo numérico compacto (para series, peso y repeticiones). Los filtros de lo que se puede
 * escribir los aplica el ViewModel, aquí solo se pinta.
 */
@Composable
fun CampoNumero(
    valor: String,
    alCambiar: (String) -> Unit,
    sugerencia: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    BasicTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier,
        singleLine = true,
        textStyle = TextStyle(color = cs.onSurface, fontSize = 16.sp, textAlign = TextAlign.Center),
        cursorBrush = SolidColor(cs.primary),
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        decorationBox = { campo ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (valor.isEmpty()) {
                    Text(
                        text = sugerencia,
                        color = cs.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                campo()
            }
        }
    )
}

private val COLORES_GRUPO = listOf(
    Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFFFFB74D), Color(0xFF81C784), Color(0xFFBA68C8),
    Color(0xFF4DB6AC), Color(0xFFFF8A65), Color(0xFF7986CB), Color(0xFFA1887F), Color(0xFFF06292),
    Color(0xFF90A4AE), Color(0xFFDCE775), Color(0xFF4FC3F7)
)

fun colorDeGrupo(grupo: GrupoMuscular): Color = COLORES_GRUPO[grupo.ordinal % COLORES_GRUPO.size]

/** Círculo de color con las iniciales del grupo muscular (sustituye a las imágenes del ejercicio). */
@Composable
fun AvatarGrupo(grupo: GrupoMuscular, seleccionado: Boolean = false, tamano: Dp = 44.dp) {
    val color = colorDeGrupo(grupo)
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(if (seleccionado) MaterialTheme.colorScheme.primary else color.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        if (seleccionado) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(
                text = stringResource(grupo.etiquetaRes()).take(2).uppercase(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FilaEtiquetaValor(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
