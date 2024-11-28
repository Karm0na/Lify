package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AniadirRutinaScreen(navController: NavController) {
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
                onAddClick = { /* Acción para añadir rutina */ }
            )
            RutinaCard(
                    titulo = "Bíceps, Tríceps y Hombro",
            descripcion = "Segundo día de Arnold Split\n3x Curl de bíceps con mancuerna\n3x Extensión de tríceps\n3x Press Arnold",
            onAddClick = { /* Acción para añadir rutina */ }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón para crear nueva rutina
        BotonCrearNuevaRutina(navController)
    }
}

// Componente: Encabezado con botón de retroceso
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

// Componente: Tarjeta de rutina preconfigurada
@Composable
fun RutinaCard(titulo: String, descripcion: String, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Fondo gris oscuro
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
                    tint = Color(0xFFFFA000) // Naranja
                )
            }
        }
    }
}

// Componente: Botón "Crear nueva rutina"
@Composable
fun BotonCrearNuevaRutina(navController: NavController) {
    Button(
        onClick = { navController.navigate("nueva_rutina") }, // Navegación futura
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)) // Naranja
    ) {
        Text(
            text = "Crear nueva rutina",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}