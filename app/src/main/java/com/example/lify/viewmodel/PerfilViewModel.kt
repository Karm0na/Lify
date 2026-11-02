package com.example.lify.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lify.LifyApp
import com.example.lify.data.IdiomaApp
import com.example.lify.data.TemaApp
import com.example.lify.domain.TipoMedida
import com.example.lify.domain.UnidadPeso
import com.example.lify.domain.rachaSemanas
import com.example.lify.model.Medida
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

data class ResumenPerfil(val entrenamientos: Int = 0, val rachaSemanas: Int = 0)

enum class MensajePerfil { EXPORTADO, ERROR_EXPORTAR, RESTAURADO, ERROR_RESTAURAR }

class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val aplicacion = getApplication<LifyApp>()
    private val repo = aplicacion.repositorio
    private val prefs = aplicacion.preferencias

    val nombre: StateFlow<String> = prefs.nombre
    val unidad: StateFlow<UnidadPeso> = prefs.unidad
    val tema: StateFlow<TemaApp> = prefs.tema
    val idioma: StateFlow<IdiomaApp> = prefs.idioma

    val medidas: StateFlow<List<Medida>> =
        repo.medidas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resumen: StateFlow<ResumenPerfil> = repo.sesiones.map { sesiones ->
        ResumenPerfil(
            entrenamientos = sesiones.size,
            rachaSemanas = rachaSemanas(sesiones.map { it.inicio }, LocalDate.now(), ZoneId.systemDefault())
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenPerfil())

    private val _mensaje = MutableStateFlow<MensajePerfil?>(null)
    val mensaje: StateFlow<MensajePerfil?> = _mensaje.asStateFlow()

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    fun cambiarNombre(valor: String) = prefs.cambiarNombre(valor)

    fun cambiarUnidad(valor: UnidadPeso) = prefs.cambiarUnidad(valor)

    fun cambiarTema(valor: TemaApp) = prefs.cambiarTema(valor)

    fun cambiarIdioma(valor: IdiomaApp) = prefs.cambiarIdioma(valor)

    /** [valor] viene en la unidad que ve el usuario; el peso corporal se guarda siempre en kg. */
    fun agregarMedida(tipo: TipoMedida, valor: Double) {
        val guardado = if (tipo.esPeso) prefs.unidad.value.aKg(valor) else valor
        viewModelScope.launch { repo.agregarMedida(tipo, guardado, System.currentTimeMillis()) }
    }

    fun eliminarMedida(id: Long) {
        viewModelScope.launch { repo.eliminarMedida(id) }
    }

    // ---- copia de seguridad

    fun exportarJson(uri: Uri) = exportar(uri) {
        repo.crearBackup(prefs.nombre.value, prefs.unidad.value.name)
    }

    fun exportarCsv(uri: Uri) = exportar(uri) { repo.exportarCsv() }

    private fun exportar(uri: Uri, generar: suspend () -> String) {
        viewModelScope.launch {
            _mensaje.value = try {
                val texto = generar()
                withContext(Dispatchers.IO) {
                    aplicacion.contentResolver.openOutputStream(uri, "wt")?.use {
                        it.write(texto.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("No se pudo abrir el archivo")
                }
                MensajePerfil.EXPORTADO
            } catch (e: Exception) {
                MensajePerfil.ERROR_EXPORTAR
            }
        }
    }

    fun restaurar(uri: Uri) {
        viewModelScope.launch {
            _mensaje.value = try {
                val json = withContext(Dispatchers.IO) {
                    aplicacion.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("No se pudo abrir el archivo")
                }
                val datos = repo.restaurarBackup(json)
                datos.nombre?.let { prefs.cambiarNombre(it) }
                datos.unidad?.let { prefs.cambiarUnidad(UnidadPeso.desdeNombre(it)) }
                MensajePerfil.RESTAURADO
            } catch (e: Exception) {
                MensajePerfil.ERROR_RESTAURAR
            }
        }
    }
}
