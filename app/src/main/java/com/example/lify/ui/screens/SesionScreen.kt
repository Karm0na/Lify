package com.example.lify.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import com.example.lify.ui.etiquetaRes
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
import com.example.lify.domain.volumenKg
import com.example.lify.model.EjercicioActivo
import com.example.lify.model.SesionActiva
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.AvatarGrupo
import com.example.lify.ui.components.BotonPrimario
import com.example.lify.ui.components.BotonSecundario
import com.example.lify.ui.components.BaldosaDato
import com.example.lify.ui.components.BarraProgreso
import com.example.lify.ui.components.CampoNumero
import com.example.lify.ui.components.degradadoAcento
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloSeccion
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
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
    val terminarEntrenamiento: () -> Unit = {
        sesionVm.terminar { guardado ->
            if (guardado) {
                Toast.makeText(contexto, avisoGuardado, Toast.LENGTH_SHORT).show()
                navController.popBackStack(Rutas.RUTINAS, false)
            } else {
                dialogoSinSeries = true
            }
        }
    }

    Scaffold(
        containerColor = cs.background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BotonPrimario(
                    texto = stringResource(R.string.terminar),
                    onClick = terminarEntrenamiento,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = actual.nombre,
                        style = MaterialTheme.typography.headlineSmall,
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
                    // Cronómetro en vivo: lo primero que se mira al entrenar.
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(cs.surfaceVariant, RoundedCornerShape(50))
                            .border(1.dp, cs.outline, RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(cs.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatearDuracion((ahora - actual.inicio) / 1000),
                            color = cs.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
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
            shape = RoundedCornerShape(24.dp),
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
            shape = RoundedCornerShape(24.dp),
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
    val hechas = sesion.seriesCompletadas()
    val total = sesion.ejercicios.sumOf { it.series.size }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BaldosaDato(
                valor = formatearDuracion((ahora - sesion.inicio) / 1000),
                etiqueta = stringResource(R.string.duracion),
                modifier = Modifier.weight(1f)
            )
            BaldosaDato(
                valor = if (total > 0) "$hechas/$total" else hechas.toString(),
                etiqueta = stringResource(R.string.series_hechas),
                modifier = Modifier.weight(1f)
            )
        }
        // Volumen levantado: crece en vivo al marcar cada serie.
        val unidad = UnidadPeso.desdeNombre(sesion.unidad)
        BaldosaDato(
            valor = formatearNumero(unidad.desdeKg(sesion.volumenKg()), 0) + " " + unidad.simbolo,
            etiqueta = stringResource(R.string.volumen_levantado),
            modifier = Modifier.fillMaxWidth()
        )
        if (total > 0) {
            BarraProgreso(fraccion = hechas.toFloat() / total.toFloat())
        }
    }
}

@Composable
private fun TarjetaDescanso(restante: Long, alSumar: () -> Unit, alSaltar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val enDescanso = restante > 0
    val forma = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(degradadoAcento(), forma)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.descanso).uppercase(),
                color = cs.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (enDescanso) formatearDuracion(restante) else stringResource(R.string.descanso_terminado),
                color = cs.onPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
        if (enDescanso) {
            TextButton(onClick = alSumar) {
                Text(stringResource(R.string.mas_15_s), color = cs.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
        TextButton(onClick = alSaltar) {
            Text(
                stringResource(if (enDescanso) R.string.saltar else R.string.cerrar),
                color = cs.onPrimary,
                fontWeight = FontWeight.Bold
            )
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
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarGrupo(grupo, tamano = 52.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(text = ejercicio.nombre, color = cs.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(grupo.etiquetaRes()),
                        color = cs.onSurface,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = { sesionVm.quitarEjercicio(ejercicio.clave) }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.quitar), tint = cs.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.col_serie),
                    color = cs.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = stringResource(R.string.col_anterior),
                    color = cs.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.9f)
                )
                Text(
                    text = stringResource(R.string.peso_unidad, unidad.simbolo),
                    color = cs.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.reps),
                    color = cs.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(46.dp))
            }

            ejercicio.series.forEachIndexed { indice, serie ->
                val ref = ejercicio.referenciaDe(indice)
                val textoRef = if (ref == null) "-"
                else "${formatearNumero(unidad.desdeKg(ref.peso))}×${ref.repeticiones}"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (serie.completada) cs.primary.copy(alpha = 0.12f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (serie.completada) cs.primary.copy(alpha = 0.55f) else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (serie.completada) cs.primary.copy(alpha = 0.25f) else cs.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (indice + 1).toString(),
                            color = if (serie.completada) cs.primary else cs.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = textoRef,
                        color = cs.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.9f)
                    )
                    CampoNumero(
                        valor = serie.peso,
                        alCambiar = { sesionVm.cambiarPeso(ejercicio.clave, serie.id, it) },
                        sugerencia = ref?.let { formatearNumero(unidad.desdeKg(it.peso)) } ?: "0",
                        decimal = true,
                        grande = true,
                        completada = serie.completada,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    CampoNumero(
                        valor = serie.reps,
                        alCambiar = { sesionVm.cambiarReps(ejercicio.clave, serie.id, it) },
                        sugerencia = ref?.repeticiones?.toString() ?: "0",
                        grande = true,
                        completada = serie.completada,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .then(
                                if (serie.completada) Modifier.background(degradadoAcento(), CircleShape)
                                else Modifier.border(1.5.dp, cs.onSurfaceVariant.copy(alpha = 0.45f), CircleShape)
                            )
                            .clickable { if (!sesionVm.alternarSerie(ejercicio.clave, serie.id)) alFallarMarcar() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (serie.completada) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = stringResource(R.string.marcar_serie),
                                tint = cs.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { sesionVm.anadirSerie(ejercicio.clave) },
                    shape = RoundedCornerShape(14.dp),
                    color = cs.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.anadir_serie), color = cs.primary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                    }
                }
                val ultima = ejercicio.series.lastOrNull()
                if (ultima != null) {
                    Surface(
                        onClick = { sesionVm.quitarSerie(ejercicio.clave, ultima.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, cs.outline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.quitar_serie), color = cs.onSurface, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
