# Lify

Aplicación Android nativa para el registro de entrenamientos de gimnasio: rutinas personalizables, seguimiento serie a serie, estadísticas de progreso y copia de seguridad de los datos. Funciona sin conexión y sin cuentas de usuario.

![Plataforma](https://img.shields.io/badge/plataforma-Android%208.0%2B-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Estado](https://img.shields.io/badge/estado-en%20desarrollo-orange)

## Capturas de pantalla

<p align="center">
  <img src="docs/screenshots/screenshot_1.jpg" width="22%" alt="Pantalla 1" />
  <img src="docs/screenshots/screenshot_2.jpg" width="22%" alt="Pantalla 2" />
  <img src="docs/screenshots/screenshot_3.jpg" width="22%" alt="Pantalla 3" />
  <img src="docs/screenshots/screenshot_4.jpg" width="22%" alt="Pantalla 4" />
</p>

## Características

### Rutinas
- Catálogo de 150 ejercicios organizados en 13 grupos musculares, con buscador, filtro por grupo y favoritos.
- Creación, edición, reordenación y eliminación de rutinas propias.
- Configuración por ejercicio: series, peso y repeticiones.
- Temporizador de descanso configurable por rutina (de 30 s a 5 min).
- Rutinas preconfiguradas (Arnold Split, 3 días).

### Entrenamiento
- Inicio desde una rutina o como entrenamiento vacío.
- Marcado de series con arranque automático del descanso y edición de peso y repeticiones durante la sesión.
- Peso y repeticiones del último entrenamiento como referencia.
- Recuperación del entrenamiento en curso si el sistema cierra la aplicación.

### Estadísticas
- Días entrenados de la semana y distribución del volumen por grupo muscular.
- Mapa corporal coloreado según el volumen trabajado.
- Filtro por periodo: 7 días, 30 días, 6 meses, 1 año o histórico completo.
- Progreso por ejercicio: peso máximo, volumen y 1RM estimado.
- Historial de entrenamientos.

### Perfil y datos
- Racha semanal, número de entrenamientos y medidas corporales con historial.
- Unidades kg/lb y tema oscuro, claro o del sistema.
- Copia de seguridad en JSON (exportación y restauración) y exportación a CSV.
- Idiomas: español (por defecto) e inglés.

## Stack tecnológico

| Área | Tecnología |
| --- | --- |
| Lenguaje | Kotlin 1.9.24 |
| Interfaz | Jetpack Compose (BOM 2024.06.00), Material 3 |
| Navegación | Navigation Compose 2.7.7 |
| Persistencia | Room 2.6.1 |
| Concurrencia | Kotlin Coroutines 1.8.1 |
| Serialización | Gson 2.10.1 |
| Build | Gradle (Kotlin DSL), Android Gradle Plugin 8.6.0, version catalog |
| Testing | JUnit 4, Espresso, Compose UI Test |

## Arquitectura

El proyecto separa la lógica de dominio, la persistencia y la interfaz. La lógica de negocio (estadísticas, unidades, CSV, entrenamiento en curso) no depende de Android, lo que permite probarla con tests unitarios simples.

```
app/src/main/java/com/example/lify/
├── data/         Room (entidades y DAOs), repositorio, catálogo JSON,
│                 preferencias y copia de seguridad
├── domain/       Lógica pura sin dependencias de Android
├── model/        Modelos compartidos (Ejercicio, Rutina, Sesion...)
├── viewmodel/    ViewModels compartidos a nivel de Activity
├── navigation/   Rutas y NavHost con barra de navegación inferior
└── ui/           Pantallas y componentes Compose
                  (gráficas y mapa muscular dibujados con Canvas)
```

Decisiones de diseño:

- **Base de datos:** Room con las tablas `ejercicios`, `rutinas`, `ejercicios_rutina`, `sesiones`, `series_realizadas` y `medidas`. El catálogo (`res/raw/ejercicios.json`) se carga en la base de datos en el primer arranque.
- **Unidades:** todos los pesos se almacenan en kg; la conversión a lb es únicamente de presentación.
- **Estado del entrenamiento:** la sesión activa se serializa como borrador con Gson, de modo que sobrevive al cierre del proceso por parte del sistema.
- **Visualización:** las gráficas y el mapa muscular se implementan con Canvas, sin librerías de gráficos externas.

## Requisitos

- Android Studio Koala (2024.1) o superior
- JDK 17
- Dispositivo o emulador con Android 8.0 (API 26) o superior

Configuración del SDK: `compileSdk` y `targetSdk` 34, `minSdk` 26.

## Compilación y ejecución

```bash
git clone https://github.com/Karm0na/Lify.git
cd Lify

./gradlew assembleDebug          # genera el APK de depuración
./gradlew installDebug           # instala en el dispositivo o emulador conectado
```

## Tests

```bash
./gradlew test                   # tests unitarios (lógica de dominio)
./gradlew connectedAndroidTest   # tests de UI (requiere emulador o dispositivo)
```

Los tests unitarios cubren estadísticas, entrenamiento en curso, editor de rutinas, grupos musculares, conversión de unidades y exportación CSV. Los tests instrumentados validan el flujo principal de la aplicación.

## Hoja de ruta

- Imágenes o animaciones de ejercicios con licencia libre.
- Aviso sonoro o vibración al terminar el descanso y notificación de la sesión en curso.
- Superseries, notas por ejercicio y entrenamientos programados.
- Configuración de firma de la APK (ver `app/build.gradle.kts`) y publicación de releases.

## Contribuciones

Las sugerencias y los reportes de errores son bienvenidos a través de [Issues](https://github.com/Karm0na/Lify/issues). Para proponer cambios, abre un Pull Request describiendo la modificación.

## Licencia

Pendiente de definir. Hasta que se añada un archivo `LICENSE`, todos los derechos están reservados.

## Autor

**Karm0na** - [github.com/Karm0na](https://github.com/Karm0na)
