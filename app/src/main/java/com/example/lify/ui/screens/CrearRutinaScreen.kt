package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Modelo para representar un ejercicio con sus detalles
data class EjercicioSeleccionado(
    val nombre: String,
    var series: Int = 1,
    var peso: Int = 0,
    var repeticiones: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRutinaScreen(
    navController: NavHostController,
    ejerciciosSeleccionados: List<EjercicioSeleccionado>
) {
    // Estado para el título de la rutina
    var tituloRutina by remember { mutableStateOf("") }

    // Estado para el temporizador de descanso
    var temporizador by remember { mutableStateOf("Apagado") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Crear Rutina",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 18.dp)
                        )
                    }
                },
                navigationIcon = {
                    Text(
                        text = "Cancelar",
                        color = Color(0xFFFFA000),
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(8.dp)
                    )
                },
                actions = {
                    Text(
                        text = "Aceptar",
                        color = Color(0xFFFFA000),
                        modifier = Modifier
                            .clickable {
                                // Procesar la rutina
                                println("Rutina creada: $tituloRutina con ejercicios: $ejerciciosSeleccionados")
                                navController.popBackStack()
                            }
                            .padding(8.dp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF121212))
            ) {
                // Campo para el título de la rutina
                TextField(
                    value = tituloRutina,
                    onValueChange = { tituloRutina = it },
                    placeholder = { Text("Título de la rutina", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        cursorColor = Color(0xFFFFA000),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // Selector de temporizador de descanso
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { dropdownExpanded = true }
                ) {
                    Text(
                        text = "Temporizador de descanso: ",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = temporizador,
                        color = Color(0xFFFFA000),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // DropdownMenu para seleccionar tiempo de descanso
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF1E1E1E))
                ) {
                    val tiempos = listOf(
                        "30 segundos", "45 segundos", "1 minuto", "1 minuto 15 segundos",
                        "1 minuto 30 segundos", "2 minutos", "2 minutos 30 segundos",
                        "3 minutos", "3 minutos 30 segundos", "4 minutos", "4 minutos 30 segundos",
                        "5 minutos"
                    )
                    tiempos.forEach { tiempo ->
                        DropdownMenuItem(
                            text = { Text(tiempo, color = Color.White) },
                            onClick = {
                                temporizador = tiempo
                                dropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de ejercicios seleccionados
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(ejerciciosSeleccionados) { ejercicio ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF1E1E1E), shape = MaterialTheme.shapes.medium)
                                .padding(16.dp)
                        ) {
                            // Nombre del ejercicio
                            Text(
                                text = ejercicio.nombre,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Editar series, peso y repeticiones
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Series
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Serie", color = Color.Gray)
                                    TextField(
                                        value = ejercicio.series.toString(),
                                        onValueChange = { value ->
                                            ejercicio.series = value.toIntOrNull() ?: 1
                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF121212),
                                            unfocusedContainerColor = Color(0xFF121212),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.width(50.dp)
                                    )
                                }

                                // Peso
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Peso", color = Color.Gray)
                                    TextField(
                                        value = ejercicio.peso.toString(),
                                        onValueChange = { value ->
                                            ejercicio.peso = value.toIntOrNull() ?: 0
                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF121212),
                                            unfocusedContainerColor = Color(0xFF121212),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.width(50.dp)
                                    )
                                }

                                // Reps
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Reps", color = Color.Gray)
                                    TextField(
                                        value = ejercicio.repeticiones.toString(),
                                        onValueChange = { value ->
                                            ejercicio.repeticiones = value.toIntOrNull() ?: 0
                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF121212),
                                            unfocusedContainerColor = Color(0xFF121212),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.width(50.dp)
                                    )
                                }
                            }

                            // Botón para añadir serie
                            Button(
                                onClick = { /* Añadir lógica para añadir serie */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text(text = "Añadir serie", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    )
}
