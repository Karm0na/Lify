package com.example.lify.data

import android.content.Context
import androidx.room.withTransaction
import com.example.lify.data.backup.BackupData
import com.example.lify.data.backup.BackupEjercicioRutina
import com.example.lify.data.backup.BackupMedida
import com.example.lify.data.backup.BackupRutina
import com.example.lify.data.backup.BackupSerie
import com.example.lify.data.backup.BackupSesion
import com.example.lify.data.backup.VERSION_BACKUP
import com.example.lify.data.db.DetalleRutinaRow
import com.example.lify.data.db.EjercicioEnRutinaEntity
import com.example.lify.data.db.EjercicioEntity
import com.example.lify.data.db.LifyDatabase
import com.example.lify.data.db.MedidaEntity
import com.example.lify.data.db.RutinaEntity
import com.example.lify.data.db.SerieRealizadaEntity
import com.example.lify.data.db.SesionEntity
import com.example.lify.domain.FilaCsv
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.GrupoVolumen
import com.example.lify.domain.SerieHistorial
import com.example.lify.domain.TipoMedida
import com.example.lify.domain.fechaLocal
import com.example.lify.domain.generarCsv
import com.example.lify.model.ConfigEjercicio
import com.example.lify.model.Ejercicio
import com.example.lify.model.EjercicioEnRutina
import com.example.lify.model.EjercicioRealizado
import com.example.lify.model.Medida
import com.example.lify.model.Rutina
import com.example.lify.model.SerieHecha
import com.example.lify.model.Sesion
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.ZoneId

/** Única puerta de acceso a los datos: las pantallas y ViewModels nunca tocan los DAO directamente. */
class LifyRepository(private val context: Context, private val db: LifyDatabase) {
    private val ejercicioDao = db.ejercicioDao()
    private val rutinaDao = db.rutinaDao()
    private val sesionDao = db.sesionDao()
    private val medidaDao = db.medidaDao()
    private val gson = Gson()

    // ----------------------------------------------------------------------------- catálogo

    /** Carga el catálogo JSON en la base de datos la primera vez que se abre la app. */
    suspend fun asegurarCatalogo() = withContext(Dispatchers.IO) {
        if (ejercicioDao.contar() == 0) {
            val items = Catalogo.cargar(context).map {
                EjercicioEntity(
                    id = it.id,
                    nombre = it.nombre,
                    grupo = GrupoMuscular.desdeEtiqueta(it.grupoMuscular).name,
                    descripcion = it.descripcion
                )
            }
            ejercicioDao.insertarTodos(items)
        }
    }

    val ejercicios: Flow<List<Ejercicio>> =
        ejercicioDao.observarTodos().map { lista -> lista.map { it.aModelo() } }

    val ejerciciosRealizados: Flow<List<Ejercicio>> =
        ejercicioDao.observarRealizados().map { lista -> lista.map { it.aModelo() } }

    /** Devuelve los ejercicios pedidos respetando el orden de [ids]. */
    suspend fun ejerciciosPorIds(ids: List<Int>): List<Ejercicio> {
        if (ids.isEmpty()) return emptyList()
        val mapa = ejercicioDao.porIds(ids).associateBy { it.id }
        return ids.mapNotNull { mapa[it]?.aModelo() }
    }

    suspend fun alternarFavorito(id: Int, favorito: Boolean) = ejercicioDao.marcarFavorito(id, favorito)

    // ----------------------------------------------------------------------------- rutinas

    val rutinas: Flow<List<Rutina>> = combine(
        rutinaDao.observarRutinas(),
        rutinaDao.observarDetalles()
    ) { rutinas, detalles ->
        val porRutina = detalles.groupBy { it.rutinaId }
        rutinas.map { r ->
            Rutina(r.id, r.nombre, r.descansoSegundos, porRutina[r.id].orEmpty().map { it.aModelo() })
        }
    }

    /** Crea una rutina ([id] nulo) o reemplaza por completo la existente. Devuelve el id. */
    suspend fun guardarRutina(
        id: Long?,
        nombre: String,
        descansoSegundos: Int,
        ejercicios: List<ConfigEjercicio>
    ): Long = db.withTransaction {
        val rutinaId: Long
        if (id == null) {
            rutinaId = rutinaDao.insertar(RutinaEntity(nombre = nombre.trim(), descansoSegundos = descansoSegundos))
        } else {
            rutinaDao.actualizar(RutinaEntity(id = id, nombre = nombre.trim(), descansoSegundos = descansoSegundos))
            rutinaDao.borrarEjerciciosDe(id)
            rutinaId = id
        }
        rutinaDao.insertarEjercicios(
            ejercicios.mapIndexed { i, c ->
                EjercicioEnRutinaEntity(
                    rutinaId = rutinaId,
                    ejercicioId = c.ejercicioId,
                    orden = i,
                    series = c.series,
                    peso = c.peso,
                    repeticiones = c.repeticiones
                )
            }
        )
        rutinaId
    }

    suspend fun eliminarRutina(id: Long) = rutinaDao.borrar(id)

    // ----------------------------------------------------------------------------- entrenamientos

    /** Guarda un entrenamiento terminado con todas sus series. */
    suspend fun guardarSesion(
        rutinaId: Long?,
        nombre: String,
        inicio: Long,
        fin: Long,
        ejercicios: List<EjercicioRealizado>
    ): Long = db.withTransaction {
        val sesionId = sesionDao.insertar(SesionEntity(rutinaId = rutinaId, nombre = nombre, inicio = inicio, fin = fin))
        val series = ejercicios.flatMapIndexed { orden, e ->
            e.series.mapIndexed { i, s ->
                SerieRealizadaEntity(
                    sesionId = sesionId,
                    ejercicioId = e.ejercicioId,
                    orden = orden,
                    numeroSerie = i + 1,
                    peso = s.peso,
                    repeticiones = s.repeticiones
                )
            }
        }
        sesionDao.insertarSeries(series)
        sesionId
    }

    suspend fun eliminarSesion(id: Long) = sesionDao.borrar(id)

    val sesiones: Flow<List<Sesion>> = sesionDao.observarSesiones().map { lista ->
        lista.map { Sesion(it.id, it.rutinaId, it.nombre, it.inicio, it.fin) }
    }

    /** Series del último entrenamiento en que se hizo [ejercicioId] (vacío si nunca se hizo). */
    suspend fun referenciaEjercicio(ejercicioId: Int): List<SerieHecha> =
        sesionDao.ultimasSeries(ejercicioId).map { SerieHecha(it.peso, it.repeticiones) }

    fun agregadoPorGrupo(desdeMillis: Long): Flow<List<GrupoVolumen>> =
        sesionDao.observarAgregadoPorGrupo(desdeMillis).map { lista ->
            lista.map { GrupoVolumen(GrupoMuscular.desdeNombre(it.grupo), it.series, it.volumen) }
        }

    fun historialEjercicio(ejercicioId: Int): Flow<List<SerieHistorial>> =
        sesionDao.observarHistorial(ejercicioId)

    // ----------------------------------------------------------------------------- medidas

    val medidas: Flow<List<Medida>> = medidaDao.observarTodas().map { lista ->
        lista.mapNotNull { m ->
            TipoMedida.desdeNombre(m.tipo)?.let { Medida(m.id, it, m.valor, m.fecha) }
        }
    }

    suspend fun agregarMedida(tipo: TipoMedida, valor: Double, fecha: Long) {
        medidaDao.insertar(MedidaEntity(tipo = tipo.name, valor = valor, fecha = fecha))
    }

    suspend fun eliminarMedida(id: Long) = medidaDao.borrar(id)

    // ----------------------------------------------------------------------------- copia de seguridad

    suspend fun crearBackup(nombre: String, unidad: String): String = withContext(Dispatchers.IO) {
        val seriesPorSesion = sesionDao.todasLasSeries().groupBy { it.sesionId }
        val detallesPorRutina = rutinaDao.todosLosDetalles().groupBy { it.rutinaId }
        val datos = BackupData(
            version = VERSION_BACKUP,
            exportadoEn = System.currentTimeMillis(),
            nombre = nombre,
            unidad = unidad,
            favoritos = ejercicioDao.idsFavoritos(),
            rutinas = rutinaDao.todas().map { r ->
                BackupRutina(
                    nombre = r.nombre,
                    descansoSegundos = r.descansoSegundos,
                    ejercicios = detallesPorRutina[r.id].orEmpty().map {
                        BackupEjercicioRutina(it.ejercicioId, it.series, it.peso, it.repeticiones)
                    }
                )
            },
            sesiones = sesionDao.todas().map { s ->
                BackupSesion(
                    nombre = s.nombre,
                    inicio = s.inicio,
                    fin = s.fin,
                    series = seriesPorSesion[s.id].orEmpty().map {
                        BackupSerie(it.ejercicioId, it.orden, it.numeroSerie, it.peso, it.repeticiones)
                    }
                )
            },
            medidas = medidaDao.todas().map { BackupMedida(it.tipo, it.valor, it.fecha) }
        )
        gson.toJson(datos)
    }

    /**
     * Sustituye rutinas, entrenamientos, medidas y favoritos por los de la copia. Lanza
     * [IllegalArgumentException] si el archivo no es una copia válida (en ese caso no se toca nada).
     * Devuelve los datos para que quien llama aplique también los ajustes (nombre, unidad).
     */
    suspend fun restaurarBackup(json: String): BackupData = withContext(Dispatchers.IO) {
        val datos: BackupData? = try {
            gson.fromJson(json, BackupData::class.java)
        } catch (e: JsonParseException) {
            null
        }
        val version = datos?.version
        if (datos == null || version == null || version > VERSION_BACKUP) {
            throw IllegalArgumentException("Copia de seguridad no válida")
        }
        val validos = ejercicioDao.todosLosIds().toSet()

        db.withTransaction {
            rutinaDao.borrarTodas()
            sesionDao.borrarTodas()
            medidaDao.borrarTodas()
            ejercicioDao.limpiarFavoritos()

            datos.favoritos.orEmpty().filter { it in validos }.forEach { ejercicioDao.marcarFavorito(it, true) }

            for (r in datos.rutinas.orEmpty()) {
                val rutinaId = rutinaDao.insertar(
                    RutinaEntity(
                        nombre = r.nombre?.takeIf { it.isNotBlank() } ?: "Rutina",
                        descansoSegundos = r.descansoSegundos ?: 0
                    )
                )
                val ejercicios = r.ejercicios.orEmpty()
                    .filter { it.ejercicioId != null && it.ejercicioId in validos }
                    .mapIndexed { i, e ->
                        EjercicioEnRutinaEntity(
                            rutinaId = rutinaId,
                            ejercicioId = e.ejercicioId ?: 0,
                            orden = i,
                            series = e.series ?: 1,
                            peso = e.peso ?: 0.0,
                            repeticiones = e.repeticiones ?: 0
                        )
                    }
                rutinaDao.insertarEjercicios(ejercicios)
            }

            for (s in datos.sesiones.orEmpty()) {
                val inicio = s.inicio ?: continue
                val sesionId = sesionDao.insertar(
                    SesionEntity(
                        rutinaId = null,
                        nombre = s.nombre?.takeIf { it.isNotBlank() } ?: "Entrenamiento",
                        inicio = inicio,
                        fin = s.fin ?: inicio
                    )
                )
                val series = s.series.orEmpty()
                    .filter { it.ejercicioId != null && it.ejercicioId in validos }
                    .mapIndexed { i, x ->
                        SerieRealizadaEntity(
                            sesionId = sesionId,
                            ejercicioId = x.ejercicioId ?: 0,
                            orden = x.orden ?: 0,
                            numeroSerie = x.numeroSerie ?: (i + 1),
                            peso = x.peso ?: 0.0,
                            repeticiones = x.repeticiones ?: 0
                        )
                    }
                sesionDao.insertarSeries(series)
            }

            medidaDao.insertarTodas(
                datos.medidas.orEmpty().mapNotNull { m ->
                    val tipo = TipoMedida.desdeNombre(m.tipo)
                    val valor = m.valor
                    val fecha = m.fecha
                    if (tipo == null || valor == null || fecha == null) null
                    else MedidaEntity(tipo = tipo.name, valor = valor, fecha = fecha)
                }
            )
        }
        datos
    }

    /** CSV con una fila por serie realizada. */
    suspend fun exportarCsv(): String = withContext(Dispatchers.IO) {
        val zona = ZoneId.systemDefault()
        generarCsv(
            sesionDao.paraExportar().map {
                FilaCsv(
                    fecha = fechaLocal(it.inicio, zona).toString(),
                    entrenamiento = it.entrenamiento,
                    ejercicio = it.ejercicio,
                    grupo = GrupoMuscular.desdeNombre(it.grupo).etiquetaCatalogo,
                    serie = it.numeroSerie,
                    pesoKg = it.peso,
                    repeticiones = it.repeticiones
                )
            }
        )
    }
}

private fun EjercicioEntity.aModelo() =
    Ejercicio(id, nombre, GrupoMuscular.desdeNombre(grupo), descripcion, favorito)

private fun DetalleRutinaRow.aModelo() =
    EjercicioEnRutina(ejercicioId, nombre, GrupoMuscular.desdeNombre(grupo), series, peso, repeticiones)
