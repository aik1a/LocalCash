# Task Brief — S00-T01: proyecto Android base

**Sprint:** S00 — Foundation  
**Rol asignado:** Codificador (Codex)  
**Estado:** READY  
**Rama de trabajo:** `task/S00-T01-proyecto-android-base` (desde `main`)  
**Repositorio:** `aik1a/LocalCash`

## Objetivo observable

Dejar en el repositorio un proyecto Android Studio mínimo, nativo en **Java**, configurable con **Gradle Groovy DSL**, que pueda compilar y abrir una pantalla inicial vacía o con el nombre «LocalCash». No implementar funciones de negocio.

## Requisitos relacionados

- `LC-P01` — Android nativo Java.
- `BUILD-01` — compilación verificable.
- Configuración expresamente solicitada por el usuario: Android Studio Quail 4; minimum SDK **API 24 (Android 7.0 Nougat)**; XML para layouts; Gradle Groovy.

## Context Pack obligatorio

Leer, en este orden:
1. `PROJECT_ROUTER.md`
2. `AGENTS.md`
3. `PROJECT_STATE.md`
4. `docs/agent/roles/CODER.md`
5. este Task Brief
6. `docs/sprints/S00-foundation.md`
7. `ARCHITECTURE.md` (solo restricciones relevantes)
8. `docs/delivery/BUILD.md`

Inspeccionar **los archivos reales de `main`** antes de generar o reemplazar nada. No cargar la documentación completa.

## Alcance autorizado

1. Confirmar si existe ya un módulo Android; reutilizarlo si cumple. Si no existe, crear el proyecto Android básico sobre este repositorio, sin borrar documentación existente.
2. Crear módulo `app` y configuración Gradle necesaria, usando archivos `.gradle` en **Groovy** (no `.gradle.kts`) y Gradle Wrapper.
3. Configurar código fuente de aplicación en **Java**, interfaz en XML y `minSdk 24`.
4. Si es proyecto nuevo, usar `namespace`/`applicationId` `com.aik1a.localcash`. Si ya hay una configuración Android válida diferente, registrarla y no renombrar unilateralmente.
5. Incorporar `MainActivity` y layout XML mínimos únicamente para verificar que la aplicación arranca.
6. Establecer `.gitignore` adecuado para Android Studio/Gradle, sin eliminar archivos del repositorio.
7. Registrar versiones reales usadas de Gradle, Android Gradle Plugin, SDK y JDK en el handoff; actualizar `docs/delivery/BUILD.md` únicamente en lo que haya sido comprobado.

## Fuera de alcance (expresamente prohibido en S00-T01)

- SQLite, entidades de dominio, repositories y services.
- osmdroid, mapas, ubicación, biometría y permisos asociados: corresponden a tareas posteriores de S00 u otros sprints.
- Navegación entre módulos, formularios, estilo visual avanzado.
- Dependencias de Firebase, Room, Compose, Retrofit, Hilt o backend.
- Cambiar requisitos, ADR, arquitectura o `PROJECT_STATE.md`.
- Crear clases o paquetes vacíos «para preparar» capas futuras.

## Criterios de aceptación

- [ ] Los archivos del proyecto Android coexisten con la documentación preexistente.
- [ ] `app` contiene Java y XML; no se introducen Kotlin/Kotlin DSL/Compose.
- [ ] `minSdk` = 24.
- [ ] `namespace` y `applicationId` están definidos y documentados.
- [ ] El proyecto usa Gradle Wrapper y sincroniza sin errores cuando el entorno dispone del SDK/JDK apropiado.
- [ ] `./gradlew :app:assembleDebug` (o `gradlew.bat :app:assembleDebug` en Windows) finaliza correctamente cuando el entorno permite ejecutarlo.
- [ ] El resultado contiene una Activity inicial registrable/lanzable; su apertura se prueba si hay dispositivo o emulador.
- [ ] No hay implementación de módulos posteriores ni dependencias fuera de alcance.
- [ ] El Codificador entrega handoff y diff de la rama.

## Validación y evidencia

- **Build:** ejecutar `./gradlew :app:assembleDebug` o equivalente Windows. Adjuntar resultado exacto, no solo «compila».
- **Tests:** no se exigen nuevas pruebas automatizadas para este esqueleto; ejecutar las que existan si aplica. No fabricar evidencia.
- **Manual:** instalar/abrir aplicación si el dispositivo o emulador existe; si no, indicar `NO EJECUTADO` y causa.
- **Revisión:** verificar estructura de proyecto, DSL, SDK mínimo, `.gitignore`, Activity/Manifest y que no haya archivos ajenos alterados.
- Si faltan SDK, JDK o Android Studio, reportar `BLOCKED` o `PARTIAL` con error reproducible; no declarar PASS sin validación.

## Entrega esperada

1. Rama `task/S00-T01-proyecto-android-base` con commits de implementación (no fusionar a `main`).
2. Resumen de archivos agregados/modificados y SHA final.
3. Evidencia de compilación y ejecución o impedimentos explícitos.
4. `docs/delivery/BUILD.md` actualizado solo con información comprobada.
5. Handoff siguiendo `docs/agent/HANDOFF_TEMPLATE.md`, en la rama de trabajo.
6. Solicitud de auditoría independiente antes de cualquier merge.

## Reglas de escalamiento

Si aparece una incompatibilidad real entre herramientas/versiones, mantener Java + Groovy + minSdk 24 y proponer al Orquestador la combinación mínima compatible; documentar claramente la incompatibilidad antes de introducir dependencias nuevas. No cambiar alcance por cuenta propia.

**El cierre de esta tarea depende del Auditor y del Orquestador, no del Codificador.**
