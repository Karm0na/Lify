package com.example.lify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Datos de ejemplo para las rutinas
data class Rutina(val nombre: String, val descripcion: String)

val rutinas = listOf(
    Rutina("Pecho y triceps", "Press de banca, Press de Banca Inclinado, Mariposa..."),
    Rutina("Espalda y biceps", "Jalon al pecho, Remo sentado, Remo con mancuerna...")
)

@Composable
fun RutinasScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título de la pantalla
            Text(
                text = "Rutinas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Rutinas en una LazyColumn
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(rutinas) { rutina ->
                    RutinaCard(rutina)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Botón para iniciar entrenamiento vacío
            Button(
                onClick = { /* Acción para iniciar entrenamiento vacío */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text(text = "Iniciar entrenamiento vacío", color = Color.White)
            }

            // Botón para añadir una nueva rutina
            Button(
                onClick = { navController.navigate("añadir_rutina") },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA000)
                )
            ) {
                Text(text = "Añadir rutina")
            }
        }
    }
}

@Composable
fun RutinaCard(rutina: Rutina) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF333333)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = rutina.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rutina.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* Acción para empezar la rutina */ },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA000)
                )
            ) {
                Text(text = "Empezar rutina", color = Color.Black)
            }
        }
    }
}
