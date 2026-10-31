package com.example.lify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lify.R
import com.example.lify.data.RUTINAS_PRECONFIGURADAS
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.BotonPrimario
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloSeccion
import com.example.lify.viewmodel.EditorRutinaViewModel
import com.example.lify.viewmodel.EjerciciosViewModel
import com.example.lify.viewmodel.RutinasViewModel

@Composable
fun AniadirRutinaScreen(
    navController: NavController,
    rutinasVm: RutinasViewModel,
    editorVm: EditorRutinaViewModel,
    ejerciciosVm: EjerciciosViewModel
) {
    val contexto = LocalContext.current
    val catalogo by ejerciciosVm.ejercicios.collectAsState()
    val nombres = catalogo.associate { it.id to it.nombre }
    val mensajeAnadida = stringResource(R.string.rutina_anadida)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.volver),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = stringResource(R.string.anadir_rutina),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        TituloSeccion(stringResource(R.string.rutinas_preconfiguradas), Modifier.padding(vertical = 8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(RUTINAS_PRECONFIGURADAS) { plantilla ->
                val titulo = stringResource(plantilla.titulo)
                TarjetaLify {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = titulo,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(plantilla.descripcion),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            plantilla.ejercicios.forEach { item ->
                                Text(
                                    text = "${item.series}× ${nombres[item.ejercicioId] ?: ""}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        IconButton(onClick = {
                            rutinasVm.agregarPreconfigurada(titulo, plantilla)
                            Toast.makeText(contexto, mensajeAnadida, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.anadir_rutina),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        BotonPrimario(
            texto = stringResource(R.string.crear_nueva_rutina),
            onClick = {
                editorVm.nueva()
                navController.navigate(Rutas.NUEVA_RUTINA)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}
