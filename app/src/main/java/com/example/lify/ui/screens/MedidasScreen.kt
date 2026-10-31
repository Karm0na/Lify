package com.example.lify.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lify.R
import com.example.lify.domain.TipoMedida
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.filtrarDecimal
import com.example.lify.domain.formatearNumero
import com.example.lify.domain.parsearNumero
import com.example.lify.model.Medida
import com.example.lify.ui.components.BotonPrimario
import com.example.lify.ui.components.GraficaLineas
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.colorCampo
import com.example.lify.ui.etiquetaRes
import com.example.lify.viewmodel.PerfilViewModel
import java.text.DateFormat
import java.util.Date

private fun valorMostrado(m: Medida, unidad: UnidadPeso): String =
    if (m.tipo.esPeso) formatearNumero(unidad.desdeKg(m.valor)) + " " + unidad.simbolo
    else formatearNumero(m.valor) + " " + (m.tipo.unidadFija ?: "")

/** Historial de medidas corporales: añadir, ver evolución y borrar registros. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedidasScreen(navController: NavHostController, vm: PerfilViewModel) {
    val cs = MaterialTheme.colorScheme
    val medidas by vm.medidas.collectAsState()
    val unidad by vm.unidad.collectAsState()
    var dialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = cs.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.medidas_corporales),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver))
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BotonPrimario(
                    texto = stringResource(R.string.anadir_medida),
                    onClick = { dialogo = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (medidas.isEmpty()) {
                item { Text(stringResource(R.string.sin_medidas), color = cs.onSurfaceVariant) }
            }

            items(TipoMedida.values().toList().filter { t -> medidas.any { it.tipo == t } }) { tipo ->
                val registros = medidas.filter { it.tipo == tipo } // más reciente primero
                TarjetaLify {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(tipo.etiquetaRes()),
                                color = cs.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = valorMostrado(registros.first(), unidad),
                                color = cs.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (registros.size >= 2) {
                            Spacer(modifier = Modifier.height(8.dp))
                            GraficaLineas(
                                registros.reversed().map { if (tipo.esPeso) unidad.desdeKg(it.valor) else it.valor }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        registros.take(8).forEach { m ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(m.fecha)),
                                    color = cs.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = valorMostrado(m, unidad), color = cs.onSurface, fontSize = 13.sp)
                                IconButton(onClick = { vm.eliminarMedida(m.id) }) {
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
            }
        }
    }

    if (dialogo) {
        DialogoNuevaMedida(
            unidad = unidad,
            alCancelar = { dialogo = false },
            alGuardar = { tipo, valor ->
                vm.agregarMedida(tipo, valor)
                dialogo = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoNuevaMedida(
    unidad: UnidadPeso,
    alCancelar: () -> Unit,
    alGuardar: (TipoMedida, Double) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var tipo by remember { mutableStateOf(TipoMedida.PESO_CORPORAL) }
    var texto by remember { mutableStateOf("") }
    val valor = parsearNumero(texto)

    AlertDialog(
        onDismissRequest = alCancelar,
        containerColor = cs.surface,
        title = { Text(stringResource(R.string.anadir_medida)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TipoMedida.values().toList()) { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipo = t },
                            label = { Text(stringResource(t.etiquetaRes())) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cs.primary,
                                selectedLabelColor = cs.onPrimary
                            )
                        )
                    }
                }
                TextField(
                    value = texto,
                    onValueChange = { texto = filtrarDecimal(it) },
                    singleLine = true,
                    placeholder = { Text(if (tipo.esPeso) unidad.simbolo else (tipo.unidadFija ?: "")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = colorCampo()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valor != null && valor > 0.0,
                onClick = { if (valor != null) alGuardar(tipo, valor) }
            ) { Text(stringResource(R.string.guardar)) }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text(stringResource(R.string.cancelar)) }
        }
    )
}
