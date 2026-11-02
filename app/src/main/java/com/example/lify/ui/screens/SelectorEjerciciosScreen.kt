package com.example.lify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.normalizarBusqueda
import com.example.lify.ui.components.AvatarGrupo
import com.example.lify.ui.components.CabeceraPantalla
import com.example.lify.ui.components.PildoraLify
import com.example.lify.ui.components.degradadoAcento
import com.example.lify.ui.components.colorCampo
import com.example.lify.ui.components.colorDeGrupo
import com.example.lify.ui.etiquetaRes
import com.example.lify.viewmodel.EjerciciosViewModel

/**
 * Selector de ejercicios con búsqueda, filtro por grupo muscular y favoritos. Sirve tanto para
 * crear rutinas como para añadir ejercicios a un entrenamiento en curso: devuelve solo los ids.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorEjerciciosScreen(
    ejerciciosVm: EjerciciosViewModel,
    alCancelar: () -> Unit,
    alAceptar: (List<Int>) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val catalogo by ejerciciosVm.ejercicios.collectAsState()

    var busqueda by rememberSaveable { mutableStateOf("") }
    var grupoFiltro by rememberSaveable { mutableStateOf<String?>(null) }
    var soloFavoritos by rememberSaveable { mutableStateOf(false) }
    var seleccionados by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }

    val consulta = normalizarBusqueda(busqueda.trim())
    val filtrados = catalogo.filter { e ->
        (grupoFiltro == null || e.grupo.name == grupoFiltro) &&
            (!soloFavoritos || e.favorito) &&
            (consulta.isEmpty() || normalizarBusqueda(e.nombre).contains(consulta))
    }

    Scaffold(
        containerColor = cs.background,
        topBar = {
            CabeceraPantalla(
                titulo = stringResource(R.string.elegir_ejercicios),
                descripcionCerrar = stringResource(R.string.cancelar),
                alCerrar = alCancelar,
                textoAccion = if (seleccionados.isEmpty()) stringResource(R.string.aceptar)
                else stringResource(R.string.aceptar_n, seleccionados.size),
                alAccion = { alAceptar(seleccionados) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(cs.background)
        ) {
            TextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.buscar)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = colorCampo(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, cs.outline, RoundedCornerShape(18.dp))
            )

            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    PildoraLify(
                        texto = stringResource(R.string.favoritos),
                        seleccionada = soloFavoritos,
                        alPulsar = { soloFavoritos = !soloFavoritos }
                    )
                }
                items(GrupoMuscular.values().toList()) { g ->
                    PildoraLify(
                        texto = stringResource(g.etiquetaRes()),
                        seleccionada = grupoFiltro == g.name,
                        alPulsar = { grupoFiltro = if (grupoFiltro == g.name) null else g.name }
                    )
                }
            }

            if (filtrados.isEmpty()) {
                Text(
                    text = stringResource(R.string.sin_resultados),
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtrados, key = { it.id }) { e ->
                    val marcado = e.id in seleccionados
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (marcado) cs.primary.copy(alpha = 0.10f) else cs.surface)
                            .border(
                                width = if (marcado) 1.5.dp else 1.dp,
                                color = if (marcado) cs.primary else cs.outline.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                seleccionados = if (marcado) seleccionados - e.id else seleccionados + e.id
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        AvatarGrupo(e.grupo, seleccionado = marcado)
                        Column(modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)) {
                            Text(
                                text = e.nombre,
                                color = cs.onBackground,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(e.grupo.etiquetaRes()),
                                color = colorDeGrupo(e.grupo),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(onClick = { ejerciciosVm.alternarFavorito(e) }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = stringResource(R.string.favorito),
                                tint = if (e.favorito) cs.primary else cs.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun chipColores() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
)
