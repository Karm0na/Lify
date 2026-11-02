package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.Periodo
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.formatearDuracion
import com.example.lify.domain.formatearNumero
import com.example.lify.model.Sesion
import com.example.lify.ui.components.BaldosaDato
import com.example.lify.ui.components.BarraDato
import com.example.lify.ui.components.PildoraLify
import com.example.lify.ui.components.degradadoAcento
import com.example.lify.ui.components.EjeRadar
import com.example.lify.ui.components.GraficaBarras
import com.example.lify.ui.components.GraficaRadar
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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
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

        // ---- Calendario mensual con racha
        item { CalendarioMes(ui.fechasEntrenadas, ui.rachaSemanas) }

        // ---- Selector de periodo
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Periodo.values().toList()) { p ->
                    PildoraLify(
                        texto = stringResource(p.etiquetaRes()),
                        seleccionada = periodo == p,
                        alPulsar = { vm.cambiarPeriodo(p) }
                    )
                }
            }
        }

        // ---- Resumen en baldosas
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                BaldosaDato(
                    valor = ui.entrenamientosPeriodo.toString(),
                    etiqueta = stringResource(periodo.etiquetaRes()),
                    modifier = Modifier.weight(1f)
                )
                BaldosaDato(
                    valor = ui.totalEntrenamientos.toString(),
                    etiqueta = stringResource(R.string.entrenamientos),
                    modifier = Modifier.weight(1f)
                )
                BaldosaDato(
                    valor = stringResource(R.string.racha_semanas, ui.rachaSemanas),
                    etiqueta = stringResource(R.string.racha),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ---- Volumen del periodo (dato destacado)
        item {
            TarjetaVolumen(
                valor = compactar(unidad.desdeKg(ui.volumenPeriodo)) + " " + unidad.simbolo,
                etiqueta = stringResource(R.string.metrica_volumen) + " · " + stringResource(periodo.etiquetaRes())
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
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.sin_datos_periodo), color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 24.dp))
                        }
                    } else {
                        GraficaRadar(
                            EJES_RADAR.map { EjeRadar(stringResource(it.etiquetaRes()), ui.intensidad[it] ?: 0f) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.sin_historial), color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 24.dp))
                        }
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
                                modifier = Modifier.background(cs.surfaceVariant, RoundedCornerShape(12.dp))
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
                                PildoraLify(
                                    texto = stringResource(
                                        when (m) {
                                            Metrica.PESO_MAXIMO -> R.string.metrica_peso_max
                                            Metrica.VOLUMEN -> R.string.metrica_volumen
                                            Metrica.UN_RM -> R.string.metrica_1rm
                                        }
                                    ),
                                    seleccionada = metrica == m,
                                    alPulsar = { metrica = m }
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
            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.sin_entrenamientos), color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 24.dp))
                }
            }
        }
        items(ui.recientes, key = { it.id }) { s ->
            val fecha = Instant.ofEpochMilli(s.inicio).atZone(ZoneId.systemDefault()).toLocalDate()
            TarjetaLify {
                Row(
                    modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cs.primary.copy(alpha = 0.12f))
                            .border(1.dp, cs.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = fecha.dayOfMonth.toString(),
                            color = cs.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = fecha.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                            color = cs.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
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
            shape = RoundedCornerShape(24.dp),
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

/**
 * Calendario del mes (lunes a domingo) con los días entrenados rellenos con el acento, el día de
 * hoy marcado con borde y la racha de semanas debajo. Las flechas cambian de mes.
 */
@Composable
private fun CalendarioMes(entrenados: Set<LocalDate>, rachaSemanas: Int) {
    val cs = MaterialTheme.colorScheme
    var mes by remember { mutableStateOf(YearMonth.now()) }
    val hoy = LocalDate.now()
    val cabeceras = listOf(
        stringResource(R.string.dia_lunes), stringResource(R.string.dia_martes), stringResource(R.string.dia_miercoles),
        stringResource(R.string.dia_jueves), stringResource(R.string.dia_viernes), stringResource(R.string.dia_sabado),
        stringResource(R.string.dia_domingo)
    )
    val huecoInicial = mes.atDay(1).dayOfWeek.value - 1
    val diasDelMes = mes.lengthOfMonth()
    val filas = (huecoInicial + diasDelMes + 6) / 7
    val nombreMes = mes.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.titlecase(Locale.getDefault()) } + " " + mes.year

    TarjetaLify {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { mes = mes.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = cs.primary)
                }
                Text(
                    text = nombreMes,
                    color = cs.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = { mes = mes.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = cs.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                cabeceras.forEach { c ->
                    Text(
                        text = c.take(2).uppercase(),
                        color = cs.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            for (fila in 0 until filas) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val dia = fila * 7 + col - huecoInicial + 1
                        if (dia in 1..diasDelMes) {
                            val fecha = mes.atDay(dia)
                            val hecho = entrenados.contains(fecha)
                            val esHoy = fecha == hoy
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(3.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .then(
                                        if (hecho) Modifier.background(degradadoAcento(), CircleShape)
                                        else Modifier.background(cs.background.copy(alpha = 0.55f), CircleShape)
                                    )
                                    .then(
                                        if (esHoy) Modifier.border(1.5.dp, cs.primary, CircleShape) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dia.toString(),
                                    color = if (hecho) cs.onPrimary else cs.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (hecho) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).padding(3.dp).aspectRatio(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cs.background.copy(alpha = 0.55f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "\uD83D\uDD25", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.racha) + ": " + stringResource(R.string.racha_semanas, rachaSemanas),
                    color = cs.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** Grupos que se dibujan en el radar (los 10 que se entrenan con pesas y se ven bien repartidos). */
private val EJES_RADAR = listOf(
    GrupoMuscular.PECHO, GrupoMuscular.ESPALDA, GrupoMuscular.HOMBROS, GrupoMuscular.BICEPS,
    GrupoMuscular.TRICEPS, GrupoMuscular.CORE, GrupoMuscular.CUADRICEPS, GrupoMuscular.ISQUIOTIBIALES,
    GrupoMuscular.GLUTEOS, GrupoMuscular.GEMELOS
)

/** 1.234 -> "1234", 12.345 -> "12,3K", 2.400.000 -> "2,4M": cifras cortas para dato destacado. */
private fun compactar(v: Double): String = when {
    v >= 1_000_000.0 -> formatearNumero(v / 1_000_000.0, 1) + "M"
    v >= 10_000.0 -> formatearNumero(v / 1_000.0, 1) + "K"
    else -> formatearNumero(v, 0)
}

/** Tarjeta ancha con una cifra grande de acento y su etiqueta, como las de resumen de las referencias. */
@Composable
private fun TarjetaVolumen(valor: String, etiqueta: String) {
    val cs = MaterialTheme.colorScheme
    TarjetaLify {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = etiqueta,
                    color = cs.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = valor,
                    color = cs.primary,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(cs.primary.copy(alpha = 0.14f), CircleShape)
                    .border(1.dp, cs.primary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = cs.primary, modifier = Modifier.size(26.dp))
            }
        }
    }
}
