package com.example.lify.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Catálogo de ejercicios (se carga del JSON en el primer arranque). */
@Entity(tableName = "ejercicios")
data class EjercicioEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    /** Nombre del enum [com.example.lify.domain.GrupoMuscular]. */
    val grupo: String,
    val descripcion: String,
    val favorito: Boolean = false
)

@Entity(tableName = "rutinas")
data class RutinaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descansoSegundos: Int = 0
)

@Entity(
    tableName = "ejercicios_rutina",
    foreignKeys = [
        ForeignKey(
            entity = RutinaEntity::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("rutinaId"), Index("ejercicioId")]
)
data class EjercicioEnRutinaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rutinaId: Long,
    val ejercicioId: Int,
    val orden: Int,
    val series: Int,
    /** Peso en kg. */
    val peso: Double,
    val repeticiones: Int
)

/** Entrenamiento terminado. No lleva clave foránea a la rutina: borrar una rutina no borra el historial. */
@Entity(tableName = "sesiones", indices = [Index("inicio")])
data class SesionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rutinaId: Long?,
    val nombre: String,
    val inicio: Long,
    val fin: Long
)

@Entity(
    tableName = "series_realizadas",
    foreignKeys = [
        ForeignKey(
            entity = SesionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sesionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sesionId"), Index("ejercicioId")]
)
data class SerieRealizadaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sesionId: Long,
    val ejercicioId: Int,
    /** Posición del ejercicio dentro del entrenamiento. */
    val orden: Int,
    val numeroSerie: Int,
    /** Peso en kg. */
    val peso: Double,
    val repeticiones: Int
)

@Entity(tableName = "medidas", indices = [Index("tipo")])
data class MedidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Nombre del enum [com.example.lify.domain.TipoMedida]. */
    val tipo: String,
    val valor: Double,
    val fecha: Long
)
