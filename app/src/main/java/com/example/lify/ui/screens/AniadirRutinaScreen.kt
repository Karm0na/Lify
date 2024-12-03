package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lify.data.Rutina
import com.example.lify.viewmodel.RutinasViewModel

@Composable
fun AniadirRutinaScreen(
    navController: NavController,
    rutinasViewModel: RutinasViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Fondo oscuro
            .padding(16.dp)
    ) {
        // Encabezado
        Encabezado(navController)

        // Sección de rutinas preconfiguradas
        Text(
            text = "Rutinas Preconfiguradas",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 18.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de rutinas preconfiguradas
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RutinaCard(
                titulo = "Pecho y espalda",
                descripcion = "Primer día de Arnold Split\n3x Press banca\n3x Jalón al pecho\n3x Press banca inclinado",
                onAddClick = {
                    rutinasViewModel.agregarRutina(
                        Rutina(
                            nombre = "Pecho y espalda",
                            ejercicios = listOf(
                                EjercicioSeleccionado("Press banca", 3),
                                EjercicioSeleccionado("Jalón al pecho", 3),
                                EjercicioSeleccionado("Press banca inclinado", 3)
                            )
                        )
                    )
                }
            )
            RutinaCard(
                titulo = "Bíceps, Tríceps y Hombro",
                descripcion = "Segundo día de Arnold Split\n3x Curl de bíceps con mancuerna\n3x Extensión de tríceps\n3x Press Arnold",
                onAddClick = {
                    rutinasViewModel.agregarRutina(
                        Rutina(
                            nombre = "Bíceps, Tríceps y Hombro",
                            ejercicios = listOf(
                                EjercicioSeleccionado("Curl de bíceps con mancuerna", 3),
                                EjercicioSeleccionado("Extensión de tríceps", 3),
                                EjercicioSeleccionado("Press Arnold", 3)
                            )
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón para crear nueva rutina
        BotonCrearNuevaRutina(navController)
    }
}

@Composable
fun Encabezado(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Retroceder",
                tint = Color.White
            )
        }
        Text(
            text = "Añadir rutina",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RutinaCard(titulo: String, descripcion: String, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir rutina",
                    tint = Color(0xFFFFA000)
                )
            }
        }
    }
}

@Composable
fun BotonCrearNuevaRutina(navController: NavController) {
    Button(
        onClick = { navController.navigate("nueva_rutina") },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
    ) {
        Text(
            text = "Crear nueva rutina",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
