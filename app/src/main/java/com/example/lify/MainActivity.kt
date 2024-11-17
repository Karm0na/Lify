package com.example.lify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lify.ui.screens.EstadisticasScreen
import com.example.lify.ui.screens.PerfilScreen
import com.example.lify.ui.screens.RutinasScreen
import com.example.lify.ui.theme.LifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifyTheme {
                val navController = rememberNavController()
                MainScreen(navController)
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

@Composable
fun EstadisticasScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212) // Fondo oscuro similar al de la pantalla de Rutinas
    ) {
        Text(text = "Pantalla de Estadísticas", color = Color.White)
    }
}

@Composable
fun PerfilScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212) // Fondo oscuro similar al de la pantalla de Rutinas
    ) {
        Text(text = "Pantalla de Perfil", color = Color.White)
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { CustomBottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavigationGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("rutinas", "Rutinas", R.drawable.ic_rutinas),
        BottomNavItem("estadisticas", "Estadísticas", R.drawable.ic_estadisticas),
        BottomNavItem("perfil", "Perfil", R.drawable.ic_perfil)
    )

    // Guarda la ruta seleccionada actualmente
    var selectedRoute by remember { mutableStateOf("rutinas") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212)) // Fondo oscuro
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.route == selectedRoute

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            selectedRoute = item.route
                            navController.navigate(item.route) {
                                // Configura navegación para evitar duplicados en la pila
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        indication = null, // Elimina el efecto de "ripple" o "destello"
                        interactionSource = remember { MutableInteractionSource() } // Necesario para `indication = null`
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.label,
                    tint = if (isSelected) Color(0xFFFFA000) else Color.Gray, // Naranja si está seleccionado, gris si no
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = item.label,
                    color = if (isSelected) Color(0xFFFFA000) else Color.Gray, // Naranja si está seleccionado, gris si no
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}





// Configuración de la navegación entre pantallas
@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "rutinas", modifier = modifier) {
        composable("rutinas") { RutinasScreen(navController) }
        composable("estadisticas") { EstadisticasScreen() }
        composable("perfil") { PerfilScreen() }
    }
}