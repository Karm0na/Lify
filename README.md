# 🏋️ Lify — Gym Tracker para Android

App nativa (Kotlin + Jetpack Compose, tema oscuro con acento naranja) para registrar entrenamientos de gimnasio: crea tus rutinas, entrena registrando cada serie y consulta tu progreso.

## Funcionalidades

- **Rutinas**: crea rutinas propias eligiendo ejercicios de un catálogo de 150 (con buscador, filtro por grupo muscular y favoritos), o añade rutinas preconfiguradas (Arnold Split, 3 días). Edita, reordena y elimina rutinas.
- **Configuración por ejercicio**: series, peso y repeticiones, más un temporizador de descanso por rutina (de 30 s a 5 min).
- **Entrenar**: empieza una rutina o un entrenamiento vacío. Marca series (arranca el descanso), edita peso/reps al vuelo y ve el peso/reps del último entrenamiento como referencia. Si Android cierra la app, el entrenamiento en curso se recupera.
- **Estadísticas** (con datos reales): días entrenados de la semana, mapa de músculos coloreado según el volumen, distribución por grupo muscular, filtro por periodo (7 días, 30 días, 6 meses, 1 año, todo), progreso por ejercicio (peso máximo, volumen, 1RM estimado) e historial de entrenamientos.
- **Perfil**: nombre, racha en semanas, nº de entrenamientos, medidas corporales con historial, unidades kg/lb y tema oscuro/claro/sistema.
- **Datos**: copia de seguridad en JSON (exportar/restaurar) y exportación a CSV.
- Idiomas: español (por defecto) e inglés.

## Diseño visual

- Tipografía **Inter** incluida en `res/font` (licencia OFL en `LICENSE-Inter.txt`).
- Cuerpo anatómico vectorial (frente y espalda) en `ui/components/CuerpoFormas.kt`: cada músculo es una forma
  independiente que se ilumina según el volumen trabajado. Se usa en el mapa de estadísticas, en las tarjetas de
  rutina (mini-mapa) y en los iconos circulares de cada ejercicio.
- Estadísticas: gráfico radar por grupo muscular, tarjeta de volumen total y curva suave de progreso.
- Entrenar: cronómetro en vivo arriba, botón "Terminar" grande abajo, números grandes en peso/reps y volumen total
  levantado en la sesión (se actualiza al marcar cada serie).
- Paleta oscura en negros neutros; barra inferior compacta y cabecera común (`CabeceraPantalla`) en crear rutina y selector.

## Idiomas

Inglés (predeterminado), español, francés y chino simplificado. Se cambia en Perfil → Ajustes → Idioma y se recuerda.
Las cadenas están en `res/values` (inglés), `values-es`, `values-fr` y `values-zh`; al añadir una, ponla en las cuatro.
El catálogo de ejercicios (`res/raw/ejercicios.json`) sigue en español.

## Requisitos y compilación

- Android Studio Koala (2024.1) o superior, JDK 17.
- minSdk 26 (Android 8.0), compileSdk/targetSdk 34.

```bash
./gradlew assembleDebug          # APK de depuración
./gradlew test                   # tests unitarios (lógica de dominio)
./gradlew connectedAndroidTest   # test de UI (requiere emulador/dispositivo)
```

## Arquitectura

```
app/src/main/java/com/example/lify/
├── data/        Room (entidades, DAOs), repositorio, catálogo JSON, preferencias, copia de seguridad
├── domain/      Lógica pura sin Android: estadísticas, entrenamiento en curso, unidades, CSV...
├── model/       Modelos únicos (Ejercicio, EjercicioEnRutina, Rutina, Sesion...)
├── viewmodel/   ViewModels compartidos por Activity (las pantallas comparten los mismos datos)
├── navigation/  Rutas y NavHost con barra inferior
└── ui/          Pantallas y componentes Compose (gráficas y mapa de músculos dibujados con Canvas)
```

- Base de datos Room: `ejercicios`, `rutinas`, `ejercicios_rutina`, `sesiones`, `series_realizadas`, `medidas`. El catálogo (`res/raw/ejercicios.json`) se vuelca a la base de datos en el primer arranque; sus grupos musculares están normalizados a 13 grupos fijos.
- Todos los pesos se guardan en kg; kg/lb es solo de presentación.

## Hoja de ruta

- Imágenes o animaciones de ejercicios con licencia libre.
- Sonido/vibración al acabar el descanso, notificación con la sesión en curso.
- Superseries, notas por ejercicio y entrenamientos programados.
- Publicación: configurar firma de la APK (ver `app/build.gradle.kts`) y generar releases.
