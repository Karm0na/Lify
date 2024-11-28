package com.example.lify.ui.screens

import android.content.Context
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.example.lify.R
import com.example.lify.data.Ejercicio

fun cargarEjercicios(context: Context): List<Ejercicio> {
    val inputStream = context.resources.openRawResource(R.raw.ejercicios)
    val json = inputStream.bufferedReader().use { it.readText() }
    val type = object : com.google.gson.reflect.TypeToken<List<Ejercicio>>() {}.type
    return Gson().fromJson(json, type)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaRutinaScreen(navController: NavHostController, context: Context) {
    val ejerciciosProvisional = listOf(
        "Press de Banca con Barra",
        "Press de Banca Inclinado",
        "Dominadas",
        "Peso Muerto",
        "Sentadillas"
    )

    // Estado de la lista de ejercicios
    val ejercicios = remember { mutableStateOf(cargarEjercicios(context)) }
    val busqueda = remember { mutableStateOf("") }
    val ejerciciosSeleccionados = remember { mutableStateListOf<Ejercicio>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                    text = "Crear Rutina",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 18.dp)
                ) }},
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
                                // Navegar a CrearRutinaScreen pasando los ejercicios seleccionados
                                navController.navigate("crear_rutina") {
                                    popUpTo("nueva_rutina") { inclusive = true }
                                }
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
                // Barra de búsqueda
                TextField(
                    value = busqueda.value,
                    onValueChange = { busqueda.value = it },
                    placeholder = { Text("Buscar", color = Color.Gray) },
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

                // Filtrar ejercicios según el texto de búsqueda
                val ejerciciosFiltrados = ejercicios.value.filter {
                    it.nombre.contains(busqueda.value, ignoreCase = true)
                }

                // Lista de ejercicios
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(ejerciciosFiltrados) { ejercicio ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Añadir o quitar ejercicios seleccionados
                                    if (ejerciciosSeleccionados.contains(ejercicio)) {
                                        ejerciciosSeleccionados.remove(ejercicio)
                                    } else {
                                        ejerciciosSeleccionados.add(ejercicio)
                                    }
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            // Círculo naranja para la imagen
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (ejerciciosSeleccionados.contains(ejercicio)) Color.Green
                                        else Color(0xFFFFA000),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            // Nombre del ejercicio y grupo muscular
                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = ejercicio.nombre,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = ejercicio.grupoMuscular,
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

