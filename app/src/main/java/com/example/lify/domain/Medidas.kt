package com.example.lify.domain

/** Medidas corporales que se pueden registrar. El peso se guarda en kg, el resto en cm (la grasa en %). */
enum class TipoMedida(val esPeso: Boolean, val unidadFija: String?) {
    PESO_CORPORAL(true, null),
    GRASA_CORPORAL(false, "%"),
    PECHO(false, "cm"),
    CINTURA(false, "cm"),
    CADERA(false, "cm"),
    BRAZO(false, "cm"),
    MUSLO(false, "cm");

    companion object {
        fun desdeNombre(nombre: String?): TipoMedida? = values().firstOrNull { it.name == nombre }
    }
}
