package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.R
import com.example.lify.domain.Periodo
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.formatearDuracion
import com.example.lify.domain.formatearNumero
import com.example.lify.model.Sesion
import com.example.lify.ui.components.BarraDato
import com.example.lify.ui.components.GraficaBarras
import com.example.lify.ui.components.GraficaLineas
import com.example.lify.ui.components.MapaMusculos
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloPantalla
import com.example.lify.ui.components.TituloSeccion
import com.example.lify.ui.components.colorDeGrupo
import com.example.lify.ui.etiquetaRes
import com.example.lify.viewmodel.EstadisticasViewModel
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

private enum class Metrica { PESO_MAXIMO, VOLUMEN, UN_RM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(vm: EstadisticasViewModel) {
    val cs = MaterialTheme.colorScheme
    val ui by vm.ui.collectAsState()
    val periodo by vm.periodo.collectAsState()
    val unidad by vm.unidad.collectAsState()
    val realizados by vm.ejerciciosRealizados.collectAsState()
    val ejercicioActual by vm.ejercicioActual.collectAsState()
    val progreso by vm.progreso.collectAsState()

    var metrica by remember { mutableStateOf(Metrica.PESO_MAXIMO) }
    var menuEjercicio by remember { mutableStateOf(false) }
    var sesionAEliminar by remember { mutableStateOf<Sesion?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TituloPantalla(stringResource(R.string.estadisticas_titulo)) }

        // ---- Días entrenados de la semana
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TituloSeccion(stringResource(R.string.esta_semana))
                DiasEntrenadosRow(ui.diasSemana)
            }
        }

        // ---- Selector de periodo
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Periodo.values().toList()) { p ->
                    FilterChip(
                        selected = periodo == p,
                        onClick = { vm.cambiarPeriodo(p) },
                        label = { Text(stringResource(p.etiquetaRes())) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cs.primary,
                            selectedLabelColor = cs.onPrimary
                        )
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.entrenamientos_periodo, ui.entrenamientosPeriodo),
                color = cs.onSurfaceVariant
            )
        }

        // ---- Mapa de músculos
        item {
            TarjetaLify {
                Column(modifier = Modifier.padding(16.dp)) {
                    TituloSeccion(stringResource(R.string.musculos_entrenados))
                    Spacer(modifier = Modifier.height(8.dp))
                    MapaMusculos(ui.intensidad)
                }
            }
        }

        // ---- Distribución por grupo muscular
        item {
            TarjetaLify {
                Column(modifier = Modifier.padding(16.dp)) {
                    TituloSeccion(stringResource(R.string.distribucion_musculos))
                    Spacer(modifier = Modifier.height(12.dp))
                    if (ui.distribucion.isEmpty()) {
                        Text(stringResource(R.string.sin_datos_periodo), color = cs.onSurfaceVariant)
                    } else {
                        val maximo = ui.distribucion.maxOf { it.porcentaje }
                        GraficaBarras(
                            ui.distribucion.map {
                                BarraDato(
                                    etiqueta = stringResource(it.grupo.etiquetaRes()),
                                    fraccion = it.porcentaje / maximo,
                                    valor = "${formatearNumero(it.porcentaje.toDouble(), 0)}%",
                                    color = colorDeGrupo(it.grupo)
                                )
                            }
                        )
                    }
                }
            }
        }

        // ---- Progreso por ejercicio
        item {
            TarjetaLify {
                Column(modifier = Modifier.padding(16.dp)) {
                    TituloSeccion(stringResource(R.string.progreso_ejercicio))
                    Spacer(modifier = Modifier.height(8.dp))
                    if (realizados.isEmpty() || ejercicioActual == null) {
                        Text(stringResource(R.string.sin_historial), color = cs.onSurfaceVariant)
                    } else {
                        Box {
                            TextButton(onClick = { menuEjercicio = true }) {
                                Text(
                                    text = realizados.firstOrNull { it.id == ejercicioActual }?.nombre ?: "",
                                    color = cs.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DropdownMenu(
                                expanded = menuEjercicio,
                                onDismissRequest = { menuEjercicio = false },
                                modifier = Modifier.background(cs.surfaceVariant)
                            ) {
                                realizados.forEach { e ->
                                    DropdownMenuItem(
                                        text = { Text(e.nombre) },
                                        onClick = {
                                            vm.elegirEjercicio(e.id)
                                            menuEjercicio = false
                                        }
                                    )
                                }
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(Metrica.values().toList()) { m ->
                                FilterChip(
                                    selected = metrica == m,
                                    onClick = { metrica = m },
                                    label = {
                                        Text(
                                            stringResource(
                                                when (m) {
                                                    Metrica.PESO_MAXIMO -> R.string.metrica_peso_max
                                                    Metrica.VOLUMEN -> R.string.metrica_volumen
                                                    Metrica.UN_RM -> R.string.metrica_1rm
                                                }
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = cs.primary,
                                        selectedLabelColor = cs.onPrimary
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        val valores = progreso.map {
                            unidad.desdeKg(
                                when (metrica) {
                                    Metrica.PESO_MAXIMO -> it.pesoMaximo
                                    Metrica.VOLUMEN -> it.volumen
                                    Metrica.UN_RM -> it.unRmEstimado
                                }
                            )
                        }
                        if (valores.isEmpty()) {
                            Text(stringResource(R.string.sin_historial), color = cs.onSurfaceVariant)
                        } else {
                            GraficaLineas(valores)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(
                                    R.string.ultimo_y_mejor,
                                    formatearNumero(valores.last()) + " " + unidad.simbolo,
                                    formatearNumero(valores.max()) + " " + unidad.simbolo
                                ),
                                color = cs.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // ---- Entrenamientos recientes
        item { TituloSeccion(stringResource(R.string.entrenamientos_recientes)) }
        if (ui.recientes.isEmpty()) {
            item { Text(stringResource(R.string.sin_entrenamientos), color = cs.onSurfaceVariant) }
        }
        items(ui.recientes, key = { it.id }) { s ->
            TarjetaLify {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = s.nombre, color = cs.onSurface, fontWeight = FontWeight.Bold)
                        Text(
                            text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(s.inicio)) +
                                " · " + formatearDuracion((s.fin - s.inicio) / 1000),
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = { sesionAEliminar = s }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.eliminar),
                            tint = cs.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    val aEliminar = sesionAEliminar
    if (aEliminar != null) {
        AlertDialog(
            onDismissRequest = { sesionAEliminar = null },
            containerColor = cs.surface,
            title = { Text(stringResource(R.string.eliminar_entrenamiento_titulo)) },
            text = { Text(stringResource(R.string.eliminar_entrenamiento_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarSesion(aEliminar.id)
                    sesionAEliminar = null
                }) { Text(stringResource(R.string.eliminar), color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { sesionAEliminar = null }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }
}

/** Siete círculos (lunes a domingo): naranja si ese día de la semana actual se entrenó. */
@Composable
private fun DiasEntrenadosRow(entrenados: List<Boolean>) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DayOfWeek.values().forEachIndexed { i, dia ->
            val hecho = entrenados.getOrElse(i) { false }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (hecho) cs.primary else cs.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    color = if (hecho) cs.onPrimary else cs.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
