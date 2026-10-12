# Auditoría — S00-T02: estructura mínima de paquetes Java

## Identificación

- **Sprint:** S00 — Foundation
- **Tarea:** S00-T02 — Estructura mínima de paquetes
- **Auditor:** Agente Auditor (revisión independiente del trabajo del Codificador)
- **Fecha:** 2026-10-12
- **Tipo:** auditoría de tarea (F4)
- **Rama:** `task/S00-T02-estructura-paquetes`
- **Commit auditado:** `6cea41252f26a76af44f81f990a99f0b95cf74b1`
- **Base:** `main` en `0da5301ae1afc9a5299dd113401067db2e7fd7a7`
- **Task Brief:** `docs/tasks/active/S00-T02-estructura-paquetes.md`
- **Handoff:** `docs/handoffs/S00-T02-coder.md`
- **Evidencia:** `docs/handoffs/evidence/S00-T02/verification.json`

## Fuentes y método

Se revisaron `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, `docs/agent/roles/AUDITOR.md`, `docs/agent/CONTEXT_PACKS.md`, `docs/audits/AUDIT_MATRIX.md`, `docs/audits/AUDIT_TEMPLATE.md`, el Task Brief, `ARCHITECTURE.md`, `docs/sprints/S00-foundation.md`, el diff frente a main, los blobs GitHub fijados por SHA y el handoff/evidencia. El encargo docente permanece como fuente normativa; esta tarea solo organiza Java nativo ya existente.

Se hizo inspección independiente del árbol Git completo, comparación de blobs originales y nuevos, y equivalencia textual de Activity y Manifest revirtiendo exclusivamente las referencias autorizadas. **No se ejecutó Android Gradle ni se abrió la app.** El entorno de esta auditoría dispone de Java, pero no de instalación local de Android SDK/Gradle con la cual compilar el proyecto.

## Criterios de aceptación

| ID | Criterio | Evidencia / resultado | Estado |
|---|---|---|---|
| AC-01 | Mover MainActivity a presentation e importar R correcto | Ruta única `app/src/main/java/com/aik1a/localcash/presentation/MainActivity.java`; declara `com.aik1a.localcash.presentation`, importa `com.aik1a.localcash.R` | CUMPLE |
| AC-02 | Ruta y paquete coinciden, sin Activity duplicada | Árbol GitHub: un único `.java`, la ruta anterior ya no existe | CUMPLE |
| AC-03 | Manifest launcher válido sin alterar filtros | `.presentation.MainActivity`, `exported=true`; diff de Manifest revertido coincide exactamente con el anterior | CUMPLE (estático) |
| AC-04 | Conservar layout, texto y configuración | SHA del layout, `strings.xml`, Gradle y configuración idénticos a base; cuerpo original de Activity preservado | CUMPLE (estático) |
| AC-05 | Documentar paquetes reales sin capas vacías | `docs/design/PACKAGE_STRUCTURE.md` distingue paquete real y capas futuras; árbol no añade paquetes vacíos | CUMPLE |
| AC-06 | Sin módulos, dependencias ni cambios fuera de alcance | Diff de cinco entradas: clase movida, manifiesto, estructura documental, handoff, JSON de verificación | CUMPLE |
| AC-07 | Build y verificaciones o bloqueo reproducible registrado | Evidencia JSON declara intento de `gradlew.bat :app:assembleDebug` rechazado antes de crear proceso; sin salida de Gradle; esta auditoría no puede reproducir Android build | PARCIAL — bloqueo de validación |
| AC-08 | Commits y handoff | Rama con 2 commits sobre main; SHA del handoff y evidencia consultables | CUMPLE |

## Matriz de calidad Q01–Q08

| ID | Peso | Estado | Evidencia |
|---|---:|---|---|
| Q01 Requisitos / aceptación | 25 | CUMPLE | Implementación y límite de entorno documentado según Task Brief; AC-07 no verificado en ejecución |
| Q02 Completitud funcional | 15 | CUMPLE (estático) | Cuerpo de Activity intacto, launcher correctamente renombrado; sin comportamiento agregado |
| Q03 Arquitectura y POO | 15 | CUMPLE | Package presentation según arquitectura; sin capas vacías |
| Q04 Calidad de código | 10 | CUMPLE | Import y declaración correctos; cambio mínimo |
| Q05 Persistencia / seguridad | 10 | N/A | Fuera del alcance de T02 |
| Q06 Build / pruebas / regresión | 15 | PARCIAL | Regresión estática PASS; build/manual sin ejecución |
| Q07 Alcance / dependencias | 5 | CUMPLE | Diff estrictamente acotado |
| Q08 Documentación / handoff | 5 | CUMPLE | PACKAGE_STRUCTURE, handoff y verification JSON presentes; sin U+FFFD en archivos inspeccionados |

**Puntaje diagnóstico normalizado:** (25 + 15 + 15 + 10 + 7.5 + 5 + 5) / (100 - 10) × 100 = **91,7 / 100**. Un puntaje alto **no desbloquea** la falta de evidencia esencial.

## Evidencia de validación

### Build
- **Comando requerido:** `gradlew.bat :app:assembleDebug` o `./gradlew :app:assembleDebug`.
- **Codificador:** **NO EJECUTADO**. `verification.json` registra que `exec_command` fue rechazado por el sandbox antes de crear el proceso (`managed networking requires the elevated Windows sandbox backend`). No existe código de salida de Gradle.
- **Auditor:** **NO EJECUTADO**; entorno sin Android SDK/Gradle preparado. No hay APK nuevo para este SHA.
- **El build de S00-T01 no certifica S00-T02:** el paquete y launcher cambiaron posteriormente.

### Tests
- No existen fuentes de pruebas unitarias/instrumentadas en el árbol revisado. **Ningún caso ejecutado**; no se declara `NO-SOURCE` para el commit nuevo como si se hubiera corrido Gradle.

### Validación manual
- Instalación, apertura del launcher `com.aik1a.localcash.presentation.MainActivity` y visualización «LocalCash»: **NO EJECUTADO**.
- La comprobación manual es condicional a dispositivo/emulador funcional conforme al Task Brief.

### Regresiones estáticas verificadas por el Auditor
- Tras revertir solamente el `package` y el import `R` de MainActivity, el texto es **idéntico** a la versión original.
- Tras revertir solo `android:name` del Manifest, el documento es **idéntico** al original.
- Layout, strings y app/build.gradle tienen **SHA de blobs idénticos** entre baseline y auditado.
- Árbol completo no truncado de GitHub: **exactamente una clase Java** en el paquete nuevo.

## Hallazgos

| ID | Severidad | Evidencia | Acción |
|---|---|---|---|
| H-01 | BLOQUEO DE VALIDACIÓN (externo, no defecto de código) | No existe build de `6cea412...`; ejecución de Gradle rechazada antes de iniciar | Obtener resultado real de `assembleDebug` en un entorno con SDK/JDK adecuado; conservar commit y cambios |
| H-02 | MINOR / pendiente condicional | Lanzamiento visual no probado | Probar con dispositivo/emulador disponible; coordinar con S00-T06 si corresponde |

No hay defectos de implementación ni cambios fuera de alcance detectados en la revisión estática.

## Gates críticos

| Gate | Resultado | Fundamento |
|---|---|---|
| Requisitos estáticos | PASS | Paquete/import/launcher/estructura conformes |
| Criterios esenciales dependientes de ejecución | BLOCKED | No existe evidencia de compilación de la nueva ubicación |
| Build requerido | **BLOCKED** | No iniciado por limitación del sandbox; auditor sin toolchain Android |
| Tests requeridos | N/A | No se exigen nuevos tests; no hay tests existentes |
| Arquitectura / decisiones | PASS | Sin alteraciones funcionales |
| Seguridad / integridad | N/A | Fuera de alcance |
| Alcance / dependencias | PASS | Diff acotado |

## Veredicto

**VEREDICTO: BLOCKED** (por evidencia de build ausente, **no por código incorrecto**).

### Autorizaciones
- **Commit:** N/A; commits de trabajo ya publicados, sin observaciones estáticas que exijan corrección.
- **Merge:** **NOT_APPROVED_FOR_MERGE**, pendiente del gate de build de este SHA.
- **Avance de sprint:** **NOT_APPROVED_FOR_SPRINT_ADVANCE**; S00 sigue en curso.

### Correcciones obligatorias
- **Ninguna corrección de código indicada.** Ejecutar y registrar `assembleDebug` del commit exacto en un entorno que permita ejecutar Gradle/Android SDK. Si falla, el Codificador debe corregir **solo el defecto real** y someter el nuevo SHA a reauditoría.

### Observaciones no bloqueantes
- Ejecutar instalación/apertura manual si se dispone de dispositivo/emulador; mantener las validaciones integrales de S00-T01 asignadas a S00-T06.
- No modificar dependencias, versiones, arquitectura ni más archivos únicamente para resolver las restricciones del sandbox.

### Conclusión
El cambio solicitado satisface la revisión estática y no presenta defectos observables. Sin una compilación del commit modificado, el protocolo `AUDIT_MATRIX.md` impide declarar aprobado el gate técnico de merge. La vía de desbloqueo es una verificación de build acotada, no rehacer S00-T02. Orquestador debe coordinar evidencia, pero **no** fusionar ni cerrar hasta superar el gate.
