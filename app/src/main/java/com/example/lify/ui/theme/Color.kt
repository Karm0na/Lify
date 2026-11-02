package com.example.lify.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de Lify.
 *
 * Inspirada en las referencias visuales: fondo negro neutro (sin matiz azul), tarjetas en
 * capas (fondo -> superficie -> superficie elevada) separadas por un borde fino, textos con
 * buen contraste y un único acento cálido (ámbar) que se usa en degradado para botones y
 * elementos activos. Para cambiar el acento de toda la app basta con tocar [LifyAmbar],
 * [LifyAmbarClaro] y [LifyAmbarOscuro].
 */
val LifyAmbar = Color(0xFFFFB02E)
val LifyAmbarClaro = Color(0xFFFFCB5C)
val LifyAmbarOscuro = Color(0xFFF59300)

/** Texto que se pinta SOBRE el acento (marrón muy oscuro: más elegante que el negro puro). */
val SobreAmbar = Color(0xFF1C1200)

// Semánticos
val LifyExito = Color(0xFF4ADE80)
val LifyError = Color(0xFFFF6B6B)

// --- Tema oscuro ---
val FondoOscuro = Color(0xFF050505)
val SuperficieOscura = Color(0xFF121212)
val SuperficieVariantOscura = Color(0xFF1C1C1E)
val TextoSecundarioOscuro = Color(0xFF9A9AA2)
val BordeOscuro = Color(0xFF2C2C2F)
val TextoPrincipalOscuro = Color(0xFFF5F5F7)

// --- Tema claro ---
val FondoClaro = Color(0xFFF5F5F7)
val SuperficieClara = Color(0xFFFFFFFF)
val SuperficieVariantClara = Color(0xFFEBEBEF)
val TextoSecundarioClaro = Color(0xFF6B6B73)
val BordeClaro = Color(0xFFDCDCE0)
val TextoPrincipalClaro = Color(0xFF111111)
