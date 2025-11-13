# Informe del proyecto "Level Up"

## Resumen ejecutivo
Proyecto Android "Level Up" (módulo `:app`). Este documento resume el trabajo realizado en la parte administrativa del proyecto: configuración, gestión de versiones y estructura del módulo. No se incluyen desarrollos ni evaluaciones de la parte cliente.

## Objetivo
Documentar los alcances realizados en la administración del proyecto y dejar constancia de los elementos que explícitamente no serán desarrollados.

## Metodología
Revisión del árbol del repositorio y de la configuración de build (Gradle wrapper, `libs.versions.toml`), y generación de documentación técnica concisa con las versiones de herramientas y dependencias detectadas.

## Resultados principales
- Estructura del módulo `app` verificada.
- Versiones principales extraídas y documentadas (Gradle wrapper, AGP, Kotlin, Compose BOM y dependencias clave).

## Alcances
- Alcances realizados: gestión y coordinación de la parte administrativa del proyecto dentro del repositorio, incluyendo:
  - Estructura del módulo `app`.
  - Configuración de build (`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`).
  - Gestión de versiones y dependencias (`gradle/wrapper/gradle-wrapper.properties`, `gradle/libs.versions.toml`).

## Lo que no se hará
- No se realizará ni se incluye la parte del cliente: código, pruebas, documentación o entregables asociados a componentes cliente (UI/UX cliente, flujos de usuario, integración con usuarios finales).

## Herramientas y versiones usadas
- Sistema operativo (desarrollo): Linux (según entorno de trabajo).
- Android SDK: `/home/sebastian/Android/Sdk` (según `local.properties`).
- Gradle (wrapper): 8.13

Dependencias y versiones (tomadas de `gradle/libs.versions.toml`):
- Android Gradle Plugin (AGP): 8.12.3
- Kotlin: 2.0.21
- core-ktx: 1.17.0
- Jetpack Compose BOM: 2024.09.00
- activity-compose: 1.11.0
- lifecycle-runtime-ktx: 2.9.4
- junit: 4.13.2
- espresso-core: 3.7.0

Plugins (definidos en `gradle/libs.versions.toml`):
- com.android.application (AGP) @ 8.12.3
- org.jetbrains.kotlin.android @ 2.0.21
- org.jetbrains.kotlin.plugin.compose @ 2.0.21

Notas:
- Las versiones gestionadas por el Compose BOM se resuelven en tiempo de compilación según la versión `2024.09.00`.

## Estructura del repositorio (relevante)
- `app/` — módulo de aplicación Android.
  - `build.gradle.kts` — configuración del módulo.
  - `src/main/` — código fuente (AndroidManifest, código Kotlin, recursos).
  - `build/` — artefactos de compilación generados.
- `gradle/` — configuración del wrapper y `libs.versions.toml`.
- `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `local.properties`.

## Anexos
- Caso de estudio: `DSY1104 - Forma B tienda LEVEL-UP GAMER (1).pdf` (archivo adjunto suministrado por la cátedra/cliente).
- Rúbrica: `Ev. Parcial 2_Estudiante_Encargo_DSY1105 (1).pdf` (criterios de evaluación).
- Informe ejemplo recibido: `INFORME (1).pdf` (ejemplo proporcionado por el usuario).

## Recomendaciones y próximos pasos
- Ejecutar `./gradlew assembleDebug` localmente para validar la compilación y detectar incompatibilidades de versiones.
- Documentar procesos administrativos adicionales si se requieren: scripts de despliegue, definiciones de roles y permisos, plan de integraciones.

---

Informe generado y actualizado según el ejemplo proporcionado.
