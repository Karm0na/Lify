package com.example.lify.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.example.lify.ui.components.degradadoAcento
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lify.R
import com.example.lify.data.IdiomaApp
import com.example.lify.data.TemaApp
import com.example.lify.domain.TipoMedida
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.formatearNumero
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.BotonSecundario
import com.example.lify.ui.components.FilaEtiquetaValor
import com.example.lify.ui.components.BaldosaDato
import com.example.lify.ui.components.PildoraLify
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloPantalla
import com.example.lify.ui.components.TituloSeccion
import com.example.lify.ui.components.colorCampo
import com.example.lify.viewmodel.MensajePerfil
import com.example.lify.viewmodel.PerfilViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavHostController, vm: PerfilViewModel) {
    val cs = MaterialTheme.colorScheme
    val contexto = LocalContext.current
    val nombre by vm.nombre.collectAsState()
    val unidad by vm.unidad.collectAsState()
    val tema by vm.tema.collectAsState()
    val idioma by vm.idioma.collectAsState()
    val resumen by vm.resumen.collectAsState()
    val medidas by vm.medidas.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    var editandoNombre by remember { mutableStateOf(false) }
    var textoNombre by remember { mutableStateOf("") }
    var restaurarUri by remember { mutableStateOf<Uri?>(null) }

    val exportarJson = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) vm.exportarJson(uri)
    }
    val exportarCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) vm.exportarCsv(uri)
    }
    val importar = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) restaurarUri = uri
    }

    val textoMensaje = mensaje?.let {
        stringResource(
            when (it) {
                MensajePerfil.EXPORTADO -> R.string.msg_exportado
                MensajePerfil.ERROR_EXPORTAR -> R.string.msg_error_exportar
                MensajePerfil.RESTAURADO -> R.string.msg_restaurado
                MensajePerfil.ERROR_RESTAURAR -> R.string.msg_error_restaurar
            }
        )
    }
    LaunchedEffect(textoMensaje) {
        if (textoMensaje != null) {
            Toast.makeText(contexto, textoMensaje, Toast.LENGTH_LONG).show()
            vm.mensajeMostrado()
        }
    }

    val ultimoPeso = medidas.firstOrNull { it.tipo == TipoMedida.PESO_CORPORAL }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloPantalla(stringResource(R.string.perfil_titulo))

        // ---- Usuario
        TarjetaLify {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val nombreMostrado = nombre.ifBlank { stringResource(R.string.nombre_por_defecto) }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(degradadoAcento(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nombreMostrado.take(1).uppercase(),
                        color = cs.onPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = nombreMostrado,
                        color = cs.onSurface,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.entrenamientos) + ": " + resumen.entrenamientos,
                        color = cs.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = {
                    textoNombre = nombre
                    editandoNombre = true
                }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.editar),
                        tint = cs.primary
                    )
                }
            }
        }

        // ---- Racha y entrenamientos (datos reales)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MiniTarjeta(
                stringResource(R.string.racha),
                stringResource(R.string.racha_semanas, resumen.rachaSemanas),
                Modifier.weight(1f)
            )
            MiniTarjeta(
                stringResource(R.string.entrenamientos),
                resumen.entrenamientos.toString(),
                Modifier.weight(1f)
            )
        }

        // ---- Medidas corporales
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TituloSeccion(stringResource(R.string.medidas_corporales))
                Text(
                    text = stringResource(R.string.mas),
                    color = cs.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate(Rutas.MEDIDAS) }
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaLify {
                Column(modifier = Modifier.padding(16.dp)) {
                    FilaEtiquetaValor(
                        etiqueta = stringResource(R.string.peso_corporal),
                        valor = if (ultimoPeso == null) stringResource(R.string.sin_registrar)
                        else formatearNumero(unidad.desdeKg(ultimoPeso.valor)) + " " + unidad.simbolo
                    )
                }
            }
        }

        // ---- Ajustes
        TituloSeccion(stringResource(R.string.ajustes))
        TarjetaLify {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.unidad_peso), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    UnidadPeso.values().forEach { u ->
                        PildoraLify(
                            texto = u.simbolo,
                            seleccionada = unidad == u,
                            alPulsar = { vm.cambiarUnidad(u) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.tema), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    TemaApp.values().forEach { t ->
                        PildoraLify(
                            texto = stringResource(
                                when (t) {
                                    TemaApp.OSCURO -> R.string.tema_oscuro
                                    TemaApp.CLARO -> R.string.tema_claro
                                    TemaApp.SISTEMA -> R.string.tema_sistema
                                }
                            ),
                            seleccionada = tema == t,
                            alPulsar = { vm.cambiarTema(t) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.idioma), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
                IdiomaApp.values().toList().chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { i ->
                            PildoraLify(
                                texto = i.nombre,
                                seleccionada = idioma == i,
                                alPulsar = { vm.cambiarIdioma(i) }
                            )
                        }
                    }
                }
            }
        }

        // ---- Copia de seguridad
        TituloSeccion(stringResource(R.string.copia_seguridad))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario(
                texto = stringResource(R.string.exportar_copia),
                onClick = { exportarJson.launch("lify-copia-${LocalDate.now()}.json") },
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = stringResource(R.string.restaurar_copia),
                onClick = { importar.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) },
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = stringResource(R.string.exportar_csv),
                onClick = { exportarCsv.launch("lify-entrenamientos-${LocalDate.now()}.csv") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (editandoNombre) {
        AlertDialog(
            onDismissRequest = { editandoNombre = false },
            containerColor = cs.surface,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.tu_nombre)) },
            text = {
                TextField(
                    value = textoNombre,
                    onValueChange = { textoNombre = it.take(40) },
                    singleLine = true,
                    colors = colorCampo()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.cambiarNombre(textoNombre)
                    editandoNombre = false
                }) { Text(stringResource(R.string.guardar)) }
            },
            dismissButton = {
                TextButton(onClick = { editandoNombre = false }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }

    val uriRestaurar = restaurarUri
    if (uriRestaurar != null) {
        AlertDialog(
            onDismissRequest = { restaurarUri = null },
            containerColor = cs.surface,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.restaurar_titulo)) },
            text = { Text(stringResource(R.string.restaurar_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.restaurar(uriRestaurar)
                    restaurarUri = null
                }) { Text(stringResource(R.string.restaurar), color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { restaurarUri = null }) { Text(stringResource(R.string.cancelar)) }
            }
        )
    }
}

@Composable
private fun MiniTarjeta(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    BaldosaDato(valor = valor, etiqueta = etiqueta, modifier = modifier)
}
