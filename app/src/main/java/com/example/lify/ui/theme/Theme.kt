package com.example.lify.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.lify.data.TemaApp

private val EsquemaOscuro = darkColorScheme(
    primary = Naranja,
    onPrimary = Color.Black,
    secondary = Naranja,
    onSecondary = Color.Black,
    tertiary = Naranja,
    background = FondoOscuro,
    onBackground = Color.White,
    surface = SuperficieOscura,
    onSurface = Color.White,
    surfaceVariant = SuperficieVariantOscura,
    onSurfaceVariant = TextoSecundarioOscuro,
    outline = BordeOscuro,
    error = Color(0xFFCF6679)
)

private val EsquemaClaro = lightColorScheme(
    primary = Naranja,
    onPrimary = Color.Black,
    secondary = Naranja,
    onSecondary = Color.Black,
    tertiary = Naranja,
    background = FondoClaro,
    onBackground = Color(0xFF1C1B1F),
    surface = SuperficieClara,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = SuperficieVariantClara,
    onSurfaceVariant = TextoSecundarioClaro,
    outline = BordeClaro,
    error = Color(0xFFB3261E)
)

/** Tema de Lify: oscuro con acento naranja por defecto; claro o el del sistema si el usuario lo elige. */
@Composable
fun LifyTheme(
    tema: TemaApp = TemaApp.OSCURO,
    content: @Composable () -> Unit
) {
    val oscuro = when (tema) {
        TemaApp.OSCURO -> true
        TemaApp.CLARO -> false
        TemaApp.SISTEMA -> isSystemInDarkTheme()
    }
    val esquema = if (oscuro) EsquemaOscuro else EsquemaClaro

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as? Activity)?.window
            if (ventana != null) {
                ventana.statusBarColor = esquema.background.toArgb()
                ventana.navigationBarColor = esquema.background.toArgb()
                val controlador = WindowCompat.getInsetsController(ventana, vista)
                controlador.isAppearanceLightStatusBars = !oscuro
                controlador.isAppearanceLightNavigationBars = !oscuro
            }
        }
    }

    MaterialTheme(
        colorScheme = esquema,
        typography = Typography,
        content = content
    )
}
