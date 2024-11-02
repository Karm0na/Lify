package com.example.lify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lify.ui.theme.LifyTheme
import com.example.lify.ui.screens.RutinasScreen
import com.example.lify.ui.screens.AñadirRutinaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifyTheme {
                // Coloca el NavController en el contexto composable
                val navController = rememberNavController()
                // Llama a la función SetupNavGraph para configurar la navegación
                SetupNavGraph(navController = navController)
            }
        }
    }
}

// Función que configura el NavGraph para la navegación
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "rutinas") {
        composable("rutinas") { RutinasScreen(navController) }
        composable("añadir_rutina") { AñadirRutinaScreen(navController) }
    }
}

// Pantalla de Rutinas
@Composable
fun RutinasScreen(navController: NavHostController) {
    // Contenido de la pantalla de Rutinas
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(text = "Pantalla de Rutinas")

        // Botón para navegar a Añadir Rutina
        Button(onClick = { navController.navigate("añadir_rutina") }) {
            Text(text = "Añadir Rutina")
        }
    }
}

// Pantalla de Añadir Rutina
@Composable
fun AñadirRutinaScreen(navController: NavHostController) {
    // Contenido de la pantalla de Añadir Rutina
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(text = "Pantalla para Añadir Rutina")
    }
}

// Función de saludo de ejemplo (no forma parte de la navegación principal)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// Vista previa de Greeting
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LifyTheme {
        Greeting("Android")
    }
}