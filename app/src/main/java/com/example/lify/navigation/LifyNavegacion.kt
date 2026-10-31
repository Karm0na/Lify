package com.example.lify.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lify.R
import com.example.lify.ui.screens.AniadirRutinaScreen
import com.example.lify.ui.screens.CrearRutinaScreen
import com.example.lify.ui.screens.EstadisticasScreen
import com.example.lify.ui.screens.MedidasScreen
import com.example.lify.ui.screens.PerfilScreen
import com.example.lify.ui.screens.RutinasScreen
import com.example.lify.ui.screens.SelectorEjerciciosScreen
import com.example.lify.ui.screens.SesionScreen
import com.example.lify.viewmodel.EditorRutinaViewModel
import com.example.lify.viewmodel.EjerciciosViewModel
import com.example.lify.viewmodel.EstadisticasViewModel
import com.example.lify.viewmodel.PerfilViewModel
import com.example.lify.viewmodel.RutinasViewModel
import com.example.lify.viewmodel.SesionViewModel

private class Pestana(val ruta: String, val etiqueta: Int, val icono: Int)

private val PESTANAS = listOf(
    Pestana(Rutas.RUTINAS, R.string.tab_rutinas, R.drawable.ic_rutinas),
    Pestana(Rutas.ESTADISTICAS, R.string.tab_estadisticas, R.drawable.ic_estadisticas),
    Pestana(Rutas.PERFIL, R.string.tab_perfil, R.drawable.ic_perfil)
)

/**
 * Raíz de la app. Los ViewModels se crean aquí (ámbito de la Activity) y se pasan a las pantallas,
 * así todas comparten los mismos datos (rutinas, borrador de rutina, entrenamiento en curso...).
 */
@Composable
fun LifyNavegacion() {
    val navController = rememberNavController()
    val rutinasVm: RutinasViewModel = viewModel()
    val editorVm: EditorRutinaViewModel = viewModel()
    val sesionVm: SesionViewModel = viewModel()
    val ejerciciosVm: EjerciciosViewModel = viewModel()
    val estadisticasVm: EstadisticasViewModel = viewModel()
    val perfilVm: PerfilViewModel = viewModel()

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // La barra solo se ve en las tres pestañas principales.
            if (PESTANAS.any { it.ruta == rutaActual }) {
                BarraInferior(navController, rutaActual)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.RUTINAS,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.RUTINAS) { RutinasScreen(navController, rutinasVm, editorVm, sesionVm) }
            composable(Rutas.ESTADISTICAS) { EstadisticasScreen(estadisticasVm) }
            composable(Rutas.PERFIL) { PerfilScreen(navController, perfilVm) }

            composable(Rutas.ANADIR_RUTINA) { AniadirRutinaScreen(navController, rutinasVm, editorVm, ejerciciosVm) }

            // Selector de ejercicios para la rutina que se edita.
            composable(Rutas.NUEVA_RUTINA) {
                SelectorEjerciciosScreen(
                    ejerciciosVm = ejerciciosVm,
                    alCancelar = { navController.popBackStack() },
                    alAceptar = { ids ->
                        editorVm.agregarEjercicios(ids)
                        if (navController.previousBackStackEntry?.destination?.route == Rutas.CREAR_RUTINA) {
                            // Se abrió desde "Añadir ejercicios": volver a la configuración.
                            navController.popBackStack()
                        } else {
                            navController.navigate(Rutas.CREAR_RUTINA) {
                                popUpTo(Rutas.NUEVA_RUTINA) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Rutas.CREAR_RUTINA) { CrearRutinaScreen(navController, editorVm) }

            composable(Rutas.SESION) { SesionScreen(navController, sesionVm) }
            // Selector de ejercicios para el entrenamiento en curso.
            composable(Rutas.SELECTOR_SESION) {
                SelectorEjerciciosScreen(
                    ejerciciosVm = ejerciciosVm,
                    alCancelar = { navController.popBackStack() },
                    alAceptar = { ids ->
                        sesionVm.agregarEjercicios(ids)
                        navController.popBackStack()
                    }
                )
            }

            composable(Rutas.MEDIDAS) { MedidasScreen(navController, perfilVm) }
        }
    }
}

@Composable
private fun BarraInferior(navController: NavHostController, rutaActual: String?) {
    val cs = MaterialTheme.colorScheme
    NavigationBar(containerColor = cs.background, tonalElevation = 0.dp) {
        PESTANAS.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = {
                    navController.navigate(pestana.ruta) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painterResource(pestana.icono), contentDescription = null) },
                label = { Text(stringResource(pestana.etiqueta)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = cs.primary,
                    selectedTextColor = cs.primary,
                    unselectedIconColor = cs.onSurfaceVariant,
                    unselectedTextColor = cs.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
