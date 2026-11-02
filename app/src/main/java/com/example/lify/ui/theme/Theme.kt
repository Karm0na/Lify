package com.example.lify.ui.theme

import android.os.Build
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
    primary = LifyAmbar,
    onPrimary = SobreAmbar,
    secondary = LifyAmbar,
    onSecondary = SobreAmbar,
    tertiary = LifyExito,
    background = FondoOscuro,
    onBackground = TextoPrincipalOscuro,
    surface = SuperficieOscura,
    onSurface = TextoPrincipalOscuro,
    surfaceVariant = SuperficieVariantOscura,
    onSurfaceVariant = TextoSecundarioOscuro,
    outline = BordeOscuro,
    outlineVariant = BordeOscuro,
    error = LifyError
)

private val EsquemaClaro = lightColorScheme(
    primary = LifyAmbarOscuro,
    onPrimary = SobreAmbar,
    secondary = LifyAmbarOscuro,
    onSecondary = SobreAmbar,
    tertiary = Color(0xFF16A34A),
    background = FondoClaro,
    onBackground = TextoPrincipalClaro,
    surface = SuperficieClara,
    onSurface = TextoPrincipalClaro,
    surfaceVariant = SuperficieVariantClara,
    onSurfaceVariant = TextoSecundarioClaro,
    outline = BordeClaro,
    outlineVariant = BordeClaro,
    error = Color(0xFFD32F2F)
)

/** Tema de Lify: oscuro con acento ámbar por defecto; claro o el del sistema si el usuario lo elige. */
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