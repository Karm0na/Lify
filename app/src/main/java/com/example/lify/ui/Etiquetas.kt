package com.example.lify.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.lify.R
import com.example.lify.domain.GrupoMuscular
import com.example.lify.domain.Periodo
import com.example.lify.domain.TipoMedida

@StringRes
fun GrupoMuscular.etiquetaRes(): Int = when (this) {
    GrupoMuscular.PECHO -> R.string.grupo_pecho
    GrupoMuscular.ESPALDA -> R.string.grupo_espalda
    GrupoMuscular.HOMBROS -> R.string.grupo_hombros
    GrupoMuscular.BICEPS -> R.string.grupo_biceps
    GrupoMuscular.TRICEPS -> R.string.grupo_triceps
    GrupoMuscular.TRAPECIOS -> R.string.grupo_trapecios
    GrupoMuscular.CORE -> R.string.grupo_core
    GrupoMuscular.CUADRICEPS -> R.string.grupo_cuadriceps
    GrupoMuscular.ISQUIOTIBIALES -> R.string.grupo_isquiotibiales
    GrupoMuscular.GLUTEOS -> R.string.grupo_gluteos
    GrupoMuscular.GEMELOS -> R.string.grupo_gemelos
    GrupoMuscular.PIERNAS -> R.string.grupo_piernas
    GrupoMuscular.CARDIO -> R.string.grupo_cardio
}

@StringRes
fun Periodo.etiquetaRes(): Int = when (this) {
    Periodo.SIETE_DIAS -> R.string.periodo_7_dias
    Periodo.TREINTA_DIAS -> R.string.periodo_30_dias
    Periodo.SEIS_MESES -> R.string.periodo_6_meses
    Periodo.UN_ANIO -> R.string.periodo_1_anio
    Periodo.TODO -> R.string.periodo_todo
}

@StringRes
fun TipoMedida.etiquetaRes(): Int = when (this) {
    TipoMedida.PESO_CORPORAL -> R.string.medida_peso
    TipoMedida.GRASA_CORPORAL -> R.string.medida_grasa
    TipoMedida.PECHO -> R.string.medida_pecho
    TipoMedida.CINTURA -> R.string.medida_cintura
    TipoMedida.CADERA -> R.string.medida_cadera
    TipoMedida.BRAZO -> R.string.medida_brazo
    TipoMedida.MUSLO -> R.string.medida_muslo
}

/** "Apagado", "45 s", "1 min", "1 min 30 s"... */
@Composable
fun textoDescanso(segundos: Int): String = when {
    segundos <= 0 -> stringResource(R.string.descanso_apagado)
    segundos < 60 -> stringResource(R.string.descanso_segundos, segundos)
    segundos % 60 == 0 -> stringResource(R.string.descanso_minutos, segundos / 60)
    else -> stringResource(R.string.descanso_min_seg, segundos / 60, segundos % 60)
}
