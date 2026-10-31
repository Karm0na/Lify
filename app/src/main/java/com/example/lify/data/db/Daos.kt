package com.example.lify.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lify.domain.SerieHistorial
import kotlinx.coroutines.flow.Flow

/** Fila del JOIN entre ejercicios_rutina y ejercicios. */
data class DetalleRutinaRow(
    val rutinaId: Long,
    val ejercicioId: Int,
    val orden: Int,
    val series: Int,
    val peso: Double,
    val repeticiones: Int,
    val nombre: String,
    val grupo: String
)

data class GrupoAgregado(val grupo: String, val series: Int, val volumen: Double)

/** Serie con todo lo necesario para exportar a CSV. */
data class SerieExportRow(
    val inicio: Long,
    val entrenamiento: String,
    val ejercicio: String,
    val grupo: String,
    val numeroSerie: Int,
    val peso: Double,
    val repeticiones: Int
)

@Dao
interface EjercicioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(lista: List<EjercicioEntity>)

    @Query("SELECT COUNT(*) FROM ejercicios")
    suspend fun contar(): Int

    @Query("SELECT * FROM ejercicios ORDER BY nombre COLLATE NOCASE")
    fun observarTodos(): Flow<List<EjercicioEntity>>

    @Query("SELECT * FROM ejercicios WHERE id IN (:ids)")
    suspend fun porIds(ids: List<Int>): List<EjercicioEntity>

    @Query("SELECT * FROM ejercicios WHERE id = :id")
    suspend fun porId(id: Int): EjercicioEntity?

    @Query("UPDATE ejercicios SET favorito = :favorito WHERE id = :id")
    suspend fun marcarFavorito(id: Int, favorito: Boolean)

    @Query("UPDATE ejercicios SET favorito = 0")
    suspend fun limpiarFavoritos()

    @Query("SELECT id FROM ejercicios")
    suspend fun todosLosIds(): List<Int>

    @Query("SELECT id FROM ejercicios WHERE favorito = 1")
    suspend fun idsFavoritos(): List<Int>

    @Query(
        "SELECT DISTINCT e.* FROM ejercicios e " +
            "INNER JOIN series_realizadas sr ON sr.ejercicioId = e.id " +
            "ORDER BY e.nombre COLLATE NOCASE"
    )
    fun observarRealizados(): Flow<List<EjercicioEntity>>
}

@Dao
interface RutinaDao {
    @Insert
    suspend fun insertar(rutina: RutinaEntity): Long

    @Update
    suspend fun actualizar(rutina: RutinaEntity)

    @Insert
    suspend fun insertarEjercicios(lista: List<EjercicioEnRutinaEntity>)

    @Query("DELETE FROM ejercicios_rutina WHERE rutinaId = :rutinaId")
    suspend fun borrarEjerciciosDe(rutinaId: Long)

    @Query("DELETE FROM rutinas WHERE id = :id")
    suspend fun borrar(id: Long)

    @Query("DELETE FROM rutinas")
    suspend fun borrarTodas()

    @Query("SELECT * FROM rutinas ORDER BY id")
    fun observarRutinas(): Flow<List<RutinaEntity>>

    @Query("SELECT * FROM rutinas ORDER BY id")
    suspend fun todas(): List<RutinaEntity>

    @Query("SELECT * FROM rutinas WHERE id = :id")
    suspend fun porId(id: Long): RutinaEntity?

    @Query(
        "SELECT er.rutinaId AS rutinaId, er.ejercicioId AS ejercicioId, er.orden AS orden, " +
            "er.series AS series, er.peso AS peso, er.repeticiones AS repeticiones, " +
            "e.nombre AS nombre, e.grupo AS grupo " +
            "FROM ejercicios_rutina er INNER JOIN ejercicios e ON e.id = er.ejercicioId " +
            "ORDER BY er.rutinaId, er.orden"
    )
    fun observarDetalles(): Flow<List<DetalleRutinaRow>>

    @Query(
        "SELECT er.rutinaId AS rutinaId, er.ejercicioId AS ejercicioId, er.orden AS orden, " +
            "er.series AS series, er.peso AS peso, er.repeticiones AS repeticiones, " +
            "e.nombre AS nombre, e.grupo AS grupo " +
            "FROM ejercicios_rutina er INNER JOIN ejercicios e ON e.id = er.ejercicioId " +
            "ORDER BY er.rutinaId, er.orden"
    )
    suspend fun todosLosDetalles(): List<DetalleRutinaRow>
}

@Dao
interface SesionDao {
    @Insert
    suspend fun insertar(sesion: SesionEntity): Long

    @Insert
    suspend fun insertarSeries(series: List<SerieRealizadaEntity>)

    @Query("DELETE FROM sesiones WHERE id = :id")
    suspend fun borrar(id: Long)

    @Query("DELETE FROM sesiones")
    suspend fun borrarTodas()

    @Query("SELECT * FROM sesiones ORDER BY inicio DESC")
    fun observarSesiones(): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones ORDER BY inicio")
    suspend fun todas(): List<SesionEntity>

    @Query("SELECT * FROM series_realizadas ORDER BY sesionId, orden, numeroSerie")
    suspend fun todasLasSeries(): List<SerieRealizadaEntity>

    @Query(
        "SELECT e.grupo AS grupo, COUNT(*) AS series, SUM(sr.peso * sr.repeticiones) AS volumen " +
            "FROM series_realizadas sr " +
            "INNER JOIN sesiones s ON s.id = sr.sesionId " +
            "INNER JOIN ejercicios e ON e.id = sr.ejercicioId " +
            "WHERE s.inicio >= :desde GROUP BY e.grupo"
    )
    fun observarAgregadoPorGrupo(desde: Long): Flow<List<GrupoAgregado>>

    @Query(
        "SELECT s.id AS sesionId, s.inicio AS fecha, sr.peso AS peso, sr.repeticiones AS repeticiones " +
            "FROM series_realizadas sr INNER JOIN sesiones s ON s.id = sr.sesionId " +
            "WHERE sr.ejercicioId = :ejercicioId ORDER BY s.inicio"
    )
    fun observarHistorial(ejercicioId: Int): Flow<List<SerieHistorial>>

    /** Series del último entrenamiento en que se hizo el ejercicio (para mostrar "anterior"). */
    @Query(
        "SELECT * FROM series_realizadas WHERE ejercicioId = :ejercicioId AND sesionId = (" +
            "SELECT s.id FROM sesiones s INNER JOIN series_realizadas x ON x.sesionId = s.id " +
            "WHERE x.ejercicioId = :ejercicioId ORDER BY s.inicio DESC LIMIT 1) " +
            "ORDER BY numeroSerie"
    )
    suspend fun ultimasSeries(ejercicioId: Int): List<SerieRealizadaEntity>

    @Query(
        "SELECT s.inicio AS inicio, s.nombre AS entrenamiento, e.nombre AS ejercicio, e.grupo AS grupo, " +
            "sr.numeroSerie AS numeroSerie, sr.peso AS peso, sr.repeticiones AS repeticiones " +
            "FROM series_realizadas sr " +
            "INNER JOIN sesiones s ON s.id = sr.sesionId " +
            "INNER JOIN ejercicios e ON e.id = sr.ejercicioId " +
            "ORDER BY s.inicio, sr.orden, sr.numeroSerie"
    )
    suspend fun paraExportar(): List<SerieExportRow>
}

@Dao
interface MedidaDao {
    @Insert
    suspend fun insertar(medida: MedidaEntity): Long

    @Insert
    suspend fun insertarTodas(lista: List<MedidaEntity>)

    @Query("DELETE FROM medidas WHERE id = :id")
    suspend fun borrar(id: Long)

    @Query("DELETE FROM medidas")
    suspend fun borrarTodas()

    @Query("SELECT * FROM medidas ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<MedidaEntity>>

    @Query("SELECT * FROM medidas ORDER BY fecha, id")
    suspend fun todas(): List<MedidaEntity>
}
