package com.example.lify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lify.R
import com.example.lify.model.Rutina
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.BotonPrimario
import com.example.lify.ui.components.BotonSecundario
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloPantalla
import com.example.lify.ui.textoDescanso
import com.example.lify.viewmodel.EditorRutinaViewModel
import com.example.lify.viewmodel.RutinasViewModel
import com.example.lify.viewmodel.SesionViewModel

@Composable
fun RutinasScreen(
    navController: NavHostController,
    rutinasVm: RutinasViewModel,
    editorVm: EditorRutinaViewModel,
    sesionVm: SesionViewModel
) {
    val rutinas by rutinasVm.rutinas.collectAsState()
    val sesion by sesionVm.sesion.collectAsState()
    val nombreLibre = stringResource(R.string.entrenamiento_libre)

    var pendiente by remember { mutableStateOf<(() -> Unit)?>(null) }
    var rutinaAEliminar by remember { mutableStateOf<Rutina?>(null) }

    /** Si ya hay un entrenamiento en curso, pide confirmación antes de empezar otro. */
    fun empezar(accion: () -> Unit) {
        if (sesion != null) pendiente = accion else accion()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        TituloPantalla(stringResource(R.string.rutinas_titulo))
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val enCurso = sesion
            if (enCurso != null) {
                item {
                    TarjetaLify {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.entrenamiento_en_curso),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = enCurso.nombre,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            BotonPrimario(
                                texto = stringResource(R.string.reanudar),
                                onClick = { navController.navigate(Rutas.SESION) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (rutinas.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.rutinas_vacio),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(rutinas, key = { it.id }) { rutina ->
                RutinaCard(
                    rutina = rutina,
                    alEmpezar = {
                        empezar {
                            sesionVm.iniciarRutina(rutina) { navController.navigate(Rutas.SESION) }
                        }
                    },
                    alEditar = {
                        editorVm.cargar(rutina)
                        navController.navigate(Rutas.CREAR_RUTINA)
                    },
                    alEliminar = { rutinaAEliminar = rutina }
                )
            }

            item {
                BotonSecundario(
                    texto = stringResource(R.string.iniciar_vacio),
                    onClick = {
                        empezar {
                            sesionVm.iniciarVacio(nombreLibre)
                            navController.navigate(Rutas.SESION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                BotonPrimario(
                    texto = stringResource(R.string.anadir_rutina),
                    onClick = { navController.navigate(Rutas.ANADIR_RUTINA) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    val accionPendiente = pendiente
    if (accionPendiente != null) {
        AlertDialog(
            onDismissRequest = { pendiente = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.ya_hay_entrenamiento_titulo)) },
            text = { Text(stringResource(R.string.ya_hay_entrenamiento_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    sesionVm.descartar()
                    pendiente = null
                    accionPendiente()
                }) { Text(stringResource(R.string.descartar_y_empezar)) }
            },
            dismissButton = {
                TextButton(onClick = { pendiente = null }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }

    val aEliminar = rutinaAEliminar
    if (aEliminar != null) {
        AlertDialog(
            onDismissRequest = { rutinaAEliminar = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.eliminar_rutina_titulo)) },
            text = { Text(stringResource(R.string.eliminar_rutina_texto, aEliminar.nombre)) },
            confirmButton = {
                TextButton(onClick = {
                    rutinasVm.eliminar(aEliminar.id)
                    rutinaAEliminar = null
                }) { Text(stringResource(R.string.eliminar), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { rutinaAEliminar = null }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }
}

@Composable
private fun RutinaCard(
    rutina: Rutina,
    alEmpezar: () -> Unit,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    TarjetaLify {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = rutina.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { menu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.opciones),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menu,
                        onDismissRequest = { menu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.editar)) },
                            onClick = {
                                menu = false
                                alEditar()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.eliminar), color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menu = false
                                alEliminar()
                            }
                        )
                    }
                }
            }
            if (rutina.ejercicios.isNotEmpty()) {
                Text(
                    text = rutina.ejercicios.joinToString(", ") { it.nombre },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.rutina_resumen,
                    rutina.ejercicios.size,
                    textoDescanso(rutina.descansoSegundos)
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            BotonPrimario(
                texto = stringResource(R.string.empezar_rutina),
                onClick = alEmpezar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
