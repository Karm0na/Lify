package com.example.lify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.descansoRestante
import com.example.lify.domain.formatearDuracion
import com.example.lify.domain.formatearNumero
import com.example.lify.domain.referenciaDe
import com.example.lify.domain.seriesCompletadas
import com.example.lify.model.EjercicioActivo
import com.example.lify.model.SesionActiva
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.AvatarGrupo
import com.example.lify.ui.components.BotonPrimario
import com.example.lify.ui.components.BotonSecundario
import com.example.lify.ui.components.CampoNumero
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.etiquetaRes
import com.example.lify.viewmodel.SesionViewModel
import kotlinx.coroutines.delay

/** Entrenamiento en curso: cronómetro, descanso, series con peso/reps y la referencia anterior. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionScreen(
    navController: NavHostController,
    sesionVm: SesionViewModel
) {
    val cs = MaterialTheme.colorScheme
    val contexto = LocalContext.current
    val sesion by sesionVm.sesion.collectAsState()

    var dialogoSinSeries by remember { mutableStateOf(false) }
    var dialogoCancelar by remember { mutableStateOf(false) }

    val actual = sesion
    if (actual == null) {
        // El entrenamiento ya no existe (terminado o descartado): salir de la pantalla.
        LaunchedEffect(Unit) { navController.popBackStack(Rutas.RUTINAS, false) }
        return
    }

    val ahora by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(500)
            value = System.currentTimeMillis()
        }
    }
    val avisoRepeticiones = stringResource(R.string.aviso_repeticiones)
    val avisoGuardado = stringResource(R.string.entrenamiento_guardado)
    val unidad = UnidadPeso.desdeNombre(actual.unidad)

    Scaffold(
        containerColor = cs.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = actual.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                actions = {
                    Text(
                        text = stringResource(R.string.terminar),
                        color = cs.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                sesionVm.terminar { guardado ->
                                    if (guardado) {
                                        Toast.makeText(contexto, avisoGuardado, Toast.LENGTH_SHORT).show()
                                        navController.popBackStack(Rutas.RUTINAS, false)
                                    } else {
                                        dialogoSinSeries = true
                                    }
                                }
                            }
                            .padding(12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.background,
                    titleContentColor = cs.onBackground,
                    navigationIconContentColor = cs.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(cs.background),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ResumenSesion(actual, ahora)
            }

            val restante = actual.descansoRestante(ahora)
            if (restante != null) {
                item {
                    TarjetaDescanso(
                        restante = restante,
                        alSumar = { sesionVm.sumarDescanso(15) },
                        alSaltar = { sesionVm.saltarDescanso() }
                    )
                }
            }

            items(actual.ejercicios, key = { it.clave }) { ejercicio ->
                TarjetaEjercicioActivo(
                    ejercicio = ejercicio,
                    unidad = unidad,
                    sesionVm = sesionVm,
                    alFallarMarcar = {
                        Toast.makeText(contexto, avisoRepeticiones, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                BotonSecundario(
                    texto = stringResource(R.string.anadir_ejercicios),
                    onClick = { navController.navigate(Rutas.SELECTOR_SESION) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                TextButton(
                    onClick = { dialogoCancelar = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cancelar_entrenamiento), color = cs.error)
                }
            }
        }
    }

    if (dialogoSinSeries) {
        AlertDialog(
            onDismissRequest = { dialogoSinSeries = false },
            containerColor = cs.surface,
            title = { Text(stringResource(R.string.sin_series_titulo)) },
            text = { Text(stringResource(R.string.sin_series_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    dialogoSinSeries = false
                    sesionVm.descartar()
                }) { Text(stringResource(R.string.descartar), color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { dialogoSinSeries = false }) { Text(stringResource(R.string.seguir_entrenando)) }
            }
        )
    }

    if (dialogoCancelar) {
        AlertDialog(
            onDismissRequest = { dialogoCancelar = false },
            containerColor = cs.surface,
            title = { Text(stringResource(R.string.cancelar_entrenamiento_titulo)) },
            text = { Text(stringResource(R.string.cancelar_entrenamiento_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    dialogoCancelar = false
                    sesionVm.descartar()
                }) { Text(stringResource(R.string.descartar), color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { dialogoCancelar = false }) { Text(stringResource(R.string.seguir_entrenando)) }
            }
        )
    }
}

@Composable
private fun ResumenSesion(sesion: SesionActiva, ahora: Long) {
    TarjetaLify {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DatoResumen(stringResource(R.string.duracion), formatearDuracion((ahora - sesion.inicio) / 1000))
            DatoResumen(stringResource(R.string.series_hechas), sesion.seriesCompletadas().toString())
        }
    }
}

@Composable
private fun DatoResumen(etiqueta: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(text = etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
private fun TarjetaDescanso(restante: Long, alSumar: () -> Unit, alSaltar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    TarjetaLify {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.descanso),
                    color = cs.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = if (restante > 0) formatearDuracion(restante) else stringResource(R.string.descanso_terminado),
                    color = cs.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            if (restante > 0) {
                TextButton(onClick = alSumar) { Text(stringResource(R.string.mas_15_s)) }
            }
            TextButton(onClick = alSaltar) {
                Text(stringResource(if (restante > 0) R.string.saltar else R.string.cerrar))
            }
        }
    }
}

@Composable
private fun TarjetaEjercicioActivo(
    ejercicio: EjercicioActivo,
    unidad: UnidadPeso,
    sesionVm: SesionViewModel,
    alFallarMarcar: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val grupo = GrupoMuscular.desdeNombre(ejercicio.grupo)
    TarjetaLify {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarGrupo(grupo, tamano = 36.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(text = ejercicio.nombre, color = cs.onSurface, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = stringResource(grupo.etiquetaRes()),
                        color = cs.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { sesionVm.quitarEjercicio(ejercicio.clave) }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.quitar), tint = cs.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CabeceraColumna(stringResource(R.string.col_serie), Modifier.width(32.dp))
                CabeceraColumna(stringResource(R.string.col_anterior), Modifier.weight(1.2f))
                CabeceraColumna(stringResource(R.string.peso_unidad, unidad.simbolo), Modifier.weight(1f))
                CabeceraColumna(stringResource(R.string.reps), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(44.dp))
            }

            ejercicio.series.forEachIndexed { indice, serie ->
                val ref = ejercicio.referenciaDe(indice)
                val textoRef = if (ref == null) "-"
                else "${formatearNumero(unidad.desdeKg(ref.peso))}×${ref.repeticiones}"
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = (indice + 1).toString(),
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = textoRef,
                        color = cs.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.2f)
                    )
                    CampoNumero(
                        valor = serie.peso,
                        alCambiar = { sesionVm.cambiarPeso(ejercicio.clave, serie.id, it) },
                        sugerencia = ref?.let { formatearNumero(unidad.desdeKg(it.peso)) } ?: "0",
                        decimal = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    CampoNumero(
                        valor = serie.reps,
                        alCambiar = { sesionVm.cambiarReps(ejercicio.clave, serie.id, it) },
                        sugerencia = ref?.repeticiones?.toString() ?: "0",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (serie.completada) cs.primary else cs.surfaceVariant)
                            .clickable {
                                if (!sesionVm.alternarSerie(ejercicio.clave, serie.id)) alFallarMarcar()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.marcar_serie),
                            tint = if (serie.completada) cs.onPrimary else cs.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { sesionVm.anadirSerie(ejercicio.clave) }) {
                    Text(stringResource(R.string.anadir_serie))
                }
                val ultima = ejercicio.series.lastOrNull()
                if (ultima != null) {
                    TextButton(onClick = { sesionVm.quitarSerie(ejercicio.clave, ultima.id) }) {
                        Text(stringResource(R.string.quitar_serie), color = cs.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CabeceraColumna(texto: String, modifier: Modifier) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
