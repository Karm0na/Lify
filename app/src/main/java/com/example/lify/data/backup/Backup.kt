package com.example.lify.data.backup

/**
 * Formato de la copia de seguridad (JSON). Todos los campos son opcionales para que Gson pueda leer
 * copias antiguas o incompletas sin romper; la restauración valida lo que necesita.
 */
const val VERSION_BACKUP = 1

data class BackupData(
    val version: Int? = null,
    val exportadoEn: Long? = null,
    val nombre: String? = null,
    val unidad: String? = null,
    val favoritos: List<Int>? = null,
    val rutinas: List<BackupRutina>? = null,
    val sesiones: List<BackupSesion>? = null,
    val medidas: List<BackupMedida>? = null
)

data class BackupRutina(
    val nombre: String? = null,
    val descansoSegundos: Int? = null,
    val ejercicios: List<BackupEjercicioRutina>? = null
)

data class BackupEjercicioRutina(
    val ejercicioId: Int? = null,
    val series: Int? = null,
    val peso: Double? = null,
    val repeticiones: Int? = null
)

data class BackupSesion(
    val nombre: String? = null,
    val inicio: Long? = null,
    val fin: Long? = null,
    val series: List<BackupSerie>? = null
)

data class BackupSerie(
    val ejercicioId: Int? = null,
    val orden: Int? = null,
    val numeroSerie: Int? = null,
    val peso: Double? = null,
    val repeticiones: Int? = null
)

data class BackupMedida(
    val tipo: String? = null,
    val valor: Double? = null,
    val fecha: Long? = null
)
