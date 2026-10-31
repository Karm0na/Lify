package com.example.lify.data

import android.content.Context
import com.example.lify.domain.UnidadPeso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TemaApp { OSCURO, CLARO, SISTEMA }

/** Ajustes sencillos del usuario (SharedPreferences) y borrador del entrenamiento en curso. */
class Preferencias(context: Context) {
    private val sp = context.getSharedPreferences("lify_prefs", Context.MODE_PRIVATE)

    private val _nombre = MutableStateFlow(sp.getString(CLAVE_NOMBRE, "") ?: "")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _unidad = MutableStateFlow(UnidadPeso.desdeNombre(sp.getString(CLAVE_UNIDAD, null)))
    val unidad: StateFlow<UnidadPeso> = _unidad.asStateFlow()

    private val _tema = MutableStateFlow(
        TemaApp.values().firstOrNull { it.name == sp.getString(CLAVE_TEMA, null) } ?: TemaApp.OSCURO
    )
    val tema: StateFlow<TemaApp> = _tema.asStateFlow()

    fun cambiarNombre(valor: String) {
        val limpio = valor.trim().take(40)
        sp.edit().putString(CLAVE_NOMBRE, limpio).apply()
        _nombre.value = limpio
    }

    fun cambiarUnidad(valor: UnidadPeso) {
        sp.edit().putString(CLAVE_UNIDAD, valor.name).apply()
        _unidad.value = valor
    }

    fun cambiarTema(valor: TemaApp) {
        sp.edit().putString(CLAVE_TEMA, valor.name).apply()
        _tema.value = valor
    }

    fun leerBorrador(): String? = sp.getString(CLAVE_BORRADOR, null)

    fun guardarBorrador(json: String?) {
        val editor = sp.edit()
        if (json == null) editor.remove(CLAVE_BORRADOR) else editor.putString(CLAVE_BORRADOR, json)
        editor.apply()
    }

    private companion object {
        const val CLAVE_NOMBRE = "nombre"
        const val CLAVE_UNIDAD = "unidad"
        const val CLAVE_TEMA = "tema"
        const val CLAVE_BORRADOR = "borrador_sesion"
    }
}
