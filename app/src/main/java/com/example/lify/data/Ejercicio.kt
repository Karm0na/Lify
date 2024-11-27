package com.example.lify.data

import android.content.Context
import com.example.lify.R
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

data class Ejercicio(
    val id: Int,
    val nombre: String,
    val grupoMuscular: String,
    val descripcion: String
)

fun cargarEjercicios(context: Context): List<Ejercicio> {
    val inputStream = context.resources.openRawResource(R.raw.ejercicios)
    val json = inputStream.bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Ejercicio>>() {}.type
    return Gson().fromJson(json, type)
}
