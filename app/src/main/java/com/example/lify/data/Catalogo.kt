package com.example.lify.data

import android.content.Context
import com.example.lify.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/** Una entrada de res/raw/ejercicios.json. */
data class CatalogoItem(
    val id: Int,
    val nombre: String,
    val grupoMuscular: String,
    val descripcion: String
)

object Catalogo {
    fun cargar(context: Context): List<CatalogoItem> {
        val json = context.resources.openRawResource(R.raw.ejercicios).bufferedReader().use { it.readText() }
        val tipo = object : TypeToken<List<CatalogoItem>>() {}.type
        return Gson().fromJson(json, tipo)
    }
}
