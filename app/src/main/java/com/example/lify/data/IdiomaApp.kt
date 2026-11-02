package com.example.lify.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/** Idiomas de la app. El predeterminado es el inglés, con independencia del idioma del móvil. */
enum class IdiomaApp(val codigo: String, val nombre: String) {
    EN("en", "English"),
    ES("es", "Español"),
    FR("fr", "Français"),
    ZH("zh", "中文");

    companion object {
        const val CLAVE = "idioma"

        fun desdeCodigo(codigo: String?): IdiomaApp = values().firstOrNull { it.codigo == codigo } ?: EN

        /** Lee el idioma guardado directamente de las preferencias (se usa antes de que exista la app). */
        fun leer(context: Context): IdiomaApp =
            desdeCodigo(context.getSharedPreferences("lify_prefs", Context.MODE_PRIVATE).getString(CLAVE, null))

        /** Devuelve un contexto cuyos recursos están en el idioma guardado. */
        fun aplicar(base: Context): Context {
            val locale = Locale.forLanguageTag(leer(base).codigo)
            Locale.setDefault(locale)
            val config = Configuration(base.resources.configuration)
            config.setLocale(locale)
            return base.createConfigurationContext(config)
        }
    }
}
