package com.example.lify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.lify.navigation.LifyNavegacion
import com.example.lify.ui.theme.LifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencias = (application as LifyApp).preferencias
        setContent {
            val tema by preferencias.tema.collectAsState()
            LifyTheme(tema = tema) {
                LifyNavegacion()
            }
        }
    }
}
