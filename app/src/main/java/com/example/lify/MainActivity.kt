package com.example.lify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.lify.data.IdiomaApp
import com.example.lify.navigation.LifyNavegacion
import com.example.lify.ui.theme.LifyTheme

class MainActivity : ComponentActivity() {
    /** Aplica el idioma elegido en la app (inglés si no se ha elegido ninguno). */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IdiomaApp.aplicar(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencias = (application as LifyApp).preferencias
        val idiomaAplicado = IdiomaApp.leer(this)
        setContent {
            val tema by preferencias.tema.collectAsState()
            val idioma by preferencias.idioma.collectAsState()
            // Al cambiar de idioma se recrea la pantalla para recargar todos los textos.
            LaunchedEffect(idioma) {
                if (idioma != idiomaAplicado) recreate()
            }
            LifyTheme(tema = tema) {
                LifyNavegacion()
            }
        }
    }
}
