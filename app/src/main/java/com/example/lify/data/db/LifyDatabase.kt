package com.example.lify.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EjercicioEntity::class,
        RutinaEntity::class,
        EjercicioEnRutinaEntity::class,
        SesionEntity::class,
        SerieRealizadaEntity::class,
        MedidaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifyDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun sesionDao(): SesionDao
    abstract fun medidaDao(): MedidaDao

    companion object {
        fun crear(context: Context): LifyDatabase =
            Room.databaseBuilder(context.applicationContext, LifyDatabase::class.java, "lify.db").build()
    }
}
