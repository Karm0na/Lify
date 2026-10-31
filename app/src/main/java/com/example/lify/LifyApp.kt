package com.example.lify

import android.app.Application
import com.example.lify.data.LifyRepository
import com.example.lify.data.Preferencias
import com.example.lify.data.db.LifyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LifyApp : Application() {
    val baseDatos: LifyDatabase by lazy { LifyDatabase.crear(this) }
    val preferencias: Preferencias by lazy { Preferencias(this) }
    val repositorio: LifyRepository by lazy { LifyRepository(this, baseDatos) }

    private val ambito = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Carga el catálogo de ejercicios en la base de datos si es el primer arranque.
        ambito.launch { repositorio.asegurarCatalogo() }
    }
}
