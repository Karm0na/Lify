package com.example.lify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lify.R
import com.example.lify.domain.ErrorEditor
import com.example.lify.domain.OPCIONES_DESCANSO
import com.example.lify.navigation.Rutas
import com.example.lify.ui.components.CabeceraPantalla
import com.example.lify.ui.components.AvatarGrupo
import com.example.lify.ui.components.BotonSecundario
import com.example.lify.ui.components.CampoNumero
import com.example.lify.ui.components.TarjetaLify
import com.example.lify.ui.components.TituloSeccion
import com.example.lify.ui.components.colorCampo
import com.example.lify.ui.etiquetaRes
import com.example.lify.ui.textoDescanso
import com.example.lify.viewmodel.EditorRutinaViewModel

/** Crear o editar una rutina: título, temporizador de descanso y series/peso/repeticiones por ejercicio. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRutinaScreen(
    navController: NavHostController,
    editorVm: EditorRutinaViewModel
) {
    val cs = MaterialTheme.colorScheme
    val contexto = LocalContext.current
    val estado by editorVm.estado.collectAsState()
    var menuDescanso by remember { mutableStateOf(false) }

    val errorNombre = stringResource(R.string.error_sin_nombre)
    val errorEjercicios = stringResource(R.string.error_sin_ejercicios)

    Scaffold(
        containerColor = cs.background,
        topBar = {
            CabeceraPantalla(
                titulo = stringResource(
                    if (estado.rutinaId == null) R.string.crear_rutina else R.string.editar_rutina
                ),
                descripcionCerrar = stringResource(R.string.cancelar),
                alCerrar = { navController.popBackStack() },
                textoAccion = stringResource(R.string.guardar),
                alAccion = {
                    editorVm.guardar { error ->
                        when (error) {
                            null -> navController.popBackStack(Rutas.RUTINAS, false)
                            ErrorEditor.SIN_NOMBRE ->
                                Toast.makeText(contexto, errorNombre, Toast.LENGTH_SHORT).show()
                            ErrorEditor.SIN_EJERCICIOS ->
                                Toast.makeText(contexto, errorEjercicios, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
                TextField(
                    value = estado.nombre,
                    onValueChange = { editorVm.cambiarNombre(it) },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.titulo_rutina)) },
                    colors = colorCampo(),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cs.outline, RoundedCornerShape(18.dp))
                )
            }

            item {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cs.surface)
                            .border(1.dp, cs.outline.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable { menuDescanso = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.temporizador_descanso) + " ",
                            color = cs.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = textoDescanso(estado.descansoSegundos),
                            color = cs.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    DropdownMenu(
                        expanded = menuDescanso,
                        onDismissRequest = { menuDescanso = false },
                        modifier = Modifier.background(cs.surfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        OPCIONES_DESCANSO.forEach { segundos ->
                            DropdownMenuItem(
                                text = { Text(textoDescanso(segundos)) },
                                onClick = {
                                    editorVm.cambiarDescanso(segundos)
                                    menuDescanso = false
                                }
                            )
                        }
                    }
                }
            }

            items(estado.ejercicios, key = { it.clave }) { e ->
                TarjetaLify {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarGrupo(e.grupo, tamano = 36.dp)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = e.nombre,
                                    color = cs.onSurface,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(e.grupo.etiquetaRes()),
                                    color = cs.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { editorVm.mover(e.clave, -1) }) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.subir),
                                    tint = cs.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { editorVm.mover(e.clave, 1) }) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = stringResource(R.string.bajar),
                                    tint = cs.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { editorVm.quitar(e.clave) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.quitar),
                                    tint = cs.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            EtiquetaCampo(stringResource(R.string.series), Modifier.weight(1f)) {
                                CampoNumero(e.series, { editorVm.cambiarSeries(e.clave, it) }, "3")
                            }
                            EtiquetaCampo(
                                stringResource(R.string.peso_unidad, estado.unidad.simbolo),
                                Modifier.weight(1f)
                            ) {
                                CampoNumero(e.peso, { editorVm.cambiarPeso(e.clave, it) }, "0", decimal = true)
                            }
                            EtiquetaCampo(stringResource(R.string.reps), Modifier.weight(1f)) {
                                CampoNumero(e.repeticiones, { editorVm.cambiarRepeticiones(e.clave, it) }, "10")
                            }
                        }
                    }
                }
            }

            if (estado.ejercicios.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.rutina_sin_ejercicios),
                        color = cs.onSurfaceVariant
                    )
                }
            }

            item {
                BotonSecundario(
                    texto = stringResource(R.string.anadir_ejercicios),
                    onClick = { navController.navigate(Rutas.NUEVA_RUTINA) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EtiquetaCampo(
    etiqueta: String,
    modifier: Modifier = Modifier,
    campo: @Composable () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        campo()
    }
}
