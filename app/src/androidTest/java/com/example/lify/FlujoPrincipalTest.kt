package com.example.lify

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Flujo principal de punta a punta: crear una rutina eligiendo un ejercicio del catálogo y
 * verla en la lista de rutinas. Se ejecuta en un dispositivo/emulador (./gradlew connectedAndroidTest).
 */
@RunWith(AndroidJUnit4::class)
class FlujoPrincipalTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun texto(id: Int, vararg args: Any): String = rule.activity.getString(id, *args)

    private fun esperarTexto(valor: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText(valor).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun crearRutinaConUnEjercicioLaMuestraEnLaLista() {
        val titulo = "Rutina de prueba"

        esperarTexto(texto(R.string.anadir_rutina))
        rule.onNodeWithText(texto(R.string.anadir_rutina)).performClick()

        esperarTexto(texto(R.string.crear_nueva_rutina))
        rule.onNodeWithText(texto(R.string.crear_nueva_rutina)).performClick()

        // El catálogo se carga en el primer arranque: esperar a que aparezca.
        esperarTexto("Press de banca plano con barra")
        rule.onNodeWithText("Press de banca plano con barra").performClick()
        rule.onNodeWithText(texto(R.string.aceptar_n, 1)).performClick()

        esperarTexto(texto(R.string.titulo_rutina))
        rule.onNodeWithText(texto(R.string.titulo_rutina)).performTextInput(titulo)
        rule.onNodeWithText(texto(R.string.guardar)).performClick()

        esperarTexto(titulo)
    }

    @Test
    fun laPestanaDeEstadisticasSeAbre() {
        esperarTexto(texto(R.string.tab_estadisticas))
        rule.onNodeWithText(texto(R.string.tab_estadisticas)).performClick()
        esperarTexto(texto(R.string.estadisticas_titulo))
    }
}
