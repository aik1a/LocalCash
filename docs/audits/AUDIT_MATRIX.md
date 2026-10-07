# LocalCash — Matriz de Calidad y Auditoría

## 1. Propósito

Esta matriz es el instrumento canónico del Agente Auditor para evaluar tareas, correcciones y cierres de sprint de LocalCash.

La matriz **no reemplaza** los requisitos, el Task Brief ni los criterios de aceptación. Sirve para aplicar una revisión consistente y dejar evidencia comparable entre agentes y sprints.

El veredicto no se decide solo por puntaje: los **gates críticos** y la severidad de los hallazgos prevalecen.

## 2. Escala de evaluación

Cada criterio aplicable se marca como:

- **CUMPLE (1.0):** evidencia suficiente y comportamiento correcto.
- **PARCIAL (0.5):** existe implementación/evidencia, pero queda una deficiencia no crítica.
- **NO CUMPLE (0.0):** falta, contradice requisitos o la evidencia demuestra fallo.
- **N/A:** no aplica a la tarea auditada; se excluye del cálculo.

El puntaje ponderado es diagnóstico. Si una categoría completa no aplica, su peso se excluye y el resultado se normaliza sobre los pesos aplicables.

## 3. Gates críticos

Un puntaje alto **no puede compensar** un fallo crítico.

Debe emitirse `FAIL` cuando exista cualquiera de estos casos:

- requisito obligatorio o criterio de aceptación esencial no cumplido;
- build requerido que no compila;
- prueba obligatoria fallida sin justificación aceptable;
- `BLOCKER` o `MAJOR` abierto que impida el objetivo auditado;
- cambio fuera de alcance que altere funcionalidad/arquitectura sin autorización;
- uso de tecnología o dependencia expresamente no aprobada;
- vulneración de una decisión canónica/ADR aplicable;
- riesgo de integridad de datos o seguridad relevante para la tarea.

Debe emitirse `BLOCKED` cuando no exista evidencia o entorno suficiente para verificar criterios esenciales y el Auditor no pueda resolverlo de forma razonable.

## 4. Matriz base de calidad

| ID | Dimensión | Peso | Qué debe revisar el Auditor | Gate crítico |
|---|---|---:|---|---|
| Q01 | Requisitos y criterios de aceptación | 25% | Cada requisito/criterio del Task Brief tiene evidencia observable; no se inventó comportamiento; el resultado coincide con fuentes canónicas. | Sí |
| Q02 | Completitud funcional | 15% | El flujo dentro del alcance está completo; no hay stubs/TODO/placeholders que impidan el objetivo; happy path, errores y bordes relevantes están considerados. | Sí cuando afecta objetivo |
| Q03 | Arquitectura y POO | 15% | Responsabilidades y dependencias respetan `ARCHITECTURE.md`; encapsulamiento e invariantes correctos; sin SQL/lógica de negocio indebidamente en UI; sin abstracciones innecesarias. | Sí para violaciones estructurales |
| Q04 | Calidad y mantenibilidad del código | 10% | Código legible, nombres coherentes, duplicación razonable, complejidad acorde al nivel académico, sin código muerto relevante ni cambios accidentales. | No, salvo impacto funcional |
| Q05 | Persistencia, datos y seguridad | 10% | Cuando aplica: SQLite/SQLiteOpenHelper, relaciones, transacciones/integridad, manejo correcto de datos locales, biometría y permisos; no se almacenan plantillas biométricas. | Sí |
| Q06 | Build, pruebas y regresión | 15% | Build requerido exitoso; tests pertinentes pasan; casos manuales necesarios tienen evidencia; correcciones incluyen regresión razonable; no se rompen flujos relacionados. | Sí |
| Q07 | Alcance, dependencias y cambios no relacionados | 5% | Diff acotado al Task Brief; no hay features extra, refactors innecesarios ni dependencias no aprobadas. | Sí si altera alcance/stack |
| Q08 | Documentación, trazabilidad y handoff | 5% | Documentos afectados coherentes; requisito→código→prueba trazable cuando corresponda; handoff identifica cambios, evidencia, riesgos y pendientes. | Sí para cierre de sprint |

**Total base:** 100%.

## 5. Subcriterios obligatorios

### Q01 — Requisitos y aceptación
- Q01.1 Todos los criterios del Task Brief fueron revisados uno por uno.
- Q01.2 Los IDs de requisitos relacionados coinciden con `PROJECT_SPEC.md`/trazabilidad.
- Q01.3 La implementación no contradice el PDF docente, ADRs o decisiones aceptadas.
- Q01.4 Todo comportamiento requerido tiene evidencia concreta en código, test o validación manual.
- Q01.5 No se da por cumplido un requisito por descripción del Codificador sin verificar el repositorio.

### Q02 — Completitud funcional
- Q02.1 El objetivo observable de la tarea puede completarse de extremo a extremo dentro del alcance.
- Q02.2 No existen métodos vacíos, mocks permanentes, TODOs o placeholders que falseen completitud.
- Q02.3 Se manejan estados de error relevantes y entradas inválidas razonables.
- Q02.4 Los recursos/configuración necesarios para ejecutar la feature están presentes.
- Q02.5 No quedan rutas funcionales parcialmente implementadas que el usuario pueda alcanzar.

### Q03 — Arquitectura y POO
- Q03.1 La UI no contiene SQL.
- Q03.2 La UI no concentra cálculos/reglas de negocio que pertenecen al dominio.
- Q03.3 Repositories encapsulan persistencia cuando aplica.
- Q03.4 Entidades protegen estado e invariantes; evitar setters que permitan saltarse reglas.
- Q03.5 Services existen solo cuando coordinan operaciones que lo justifican.
- Q03.6 No se introducen capas/patrones por simetría o sobreingeniería.

### Q04 — Calidad de código
- Q04.1 Nombres expresan intención.
- Q04.2 Métodos/clases mantienen responsabilidades comprensibles.
- Q04.3 No existe duplicación significativa evitable dentro del cambio.
- Q04.4 No queda código comentado/muerto o debugging accidental.
- Q04.5 Manejo de null/errores es coherente con el flujo.
- Q04.6 El cambio es comprensible para un proyecto académico de segundo año.

### Q05 — Persistencia, datos y seguridad
Aplicar solo cuando corresponda.
- Q05.1 SQLite sigue siendo la fuente persistente definida.
- Q05.2 `SQLiteOpenHelper` se usa según la arquitectura aprobada.
- Q05.3 Relaciones/constraints/transacciones necesarias preservan integridad.
- Q05.4 Datos derivados no se duplican sin una decisión documentada.
- Q05.5 Operaciones multi-tabla son atómicas cuando el flujo lo requiere.
- Q05.6 Biometría usa APIs Android y no almacena huellas/plantillas.
- Q05.7 Permisos se justifican por una función real y no exceden el alcance.
- Q05.8 El comportamiento offline obligatorio no depende silenciosamente de un backend.

### Q06 — Build, tests y regresión
- Q06.1 El comando/build exigido por el Task Brief fue ejecutado o existe una limitación explícita.
- Q06.2 Tests pertinentes pasan.
- Q06.3 Los tests verifican comportamiento y no solo existencia de clases.
- Q06.4 Flujos manuales que dependan de dispositivo/emulador tienen escenario reproducible.
- Q06.5 Un bug corregido tiene prueba de regresión cuando sea razonablemente testeable.
- Q06.6 Se revisan regresiones directamente relacionadas con el cambio.

### Q07 — Alcance y dependencias
- Q07.1 El diff corresponde al Task Brief.
- Q07.2 No hay features adicionales no solicitadas.
- Q07.3 No hay refactors extensos sin necesidad.
- Q07.4 No se introducen dependencias principales sin aprobación.
- Q07.5 No aparecen Firebase, Room, Compose, backend, DI u otras tecnologías prohibidas por las decisiones vigentes.

### Q08 — Documentación y continuidad
- Q08.1 El handoff identifica rama/commit/diff exacto.
- Q08.2 La documentación técnica afectada coincide con el código real.
- Q08.3 `TRACEABILITY.md` puede actualizarse con evidencia concreta.
- Q08.4 Pendientes/limitaciones están declarados.
- Q08.5 La tarea puede continuar en otro chat sin depender de memoria privada del agente.

## 6. Matriz específica por sprint

La matriz base siempre aplica. Además, el Auditor añade estos focos según el sprint.

| Sprint | Foco adicional obligatorio |
|---|---|
| S00 Foundation | Proyecto Android real; Java; XML; Gradle/Groovy; estructura mínima; dependencias aprobadas; permisos justificados; build desde checkout limpio; ausencia de Firebase/Room/Compose/backend. |
| S01 Persistence & Domain | Entidades e invariantes; SQLiteOpenHelper; esquema/CRUD; repositories; integridad y relaciones; no SQL en UI; pruebas de dominio/persistencia. |
| S02 Security | `BiometricPrompt`; bloqueo antes de contenido financiero; cancelación/error; no almacenamiento biométrico; comportamiento compatible con capacidades del dispositivo. |
| S03 Accounts & Movements | múltiples cuentas; ingresos/egresos; asociación correcta; monto/tipo; saldo derivado según reglas aceptadas; persistencia y pruebas de cálculo. |
| S04 Geolocation & Maps | ubicación opcional; permisos; movimiento con/sin coordenadas; persistencia; visualización osmdroid; estrategia offline conforme a la decisión vigente, sin inventarla si sigue pendiente. |
| S05 Purchases & Contacts | contactos locales; compras/historial; detalles; planificación futura; total derivado; relaciones definidas; persistencia coherente. |
| S06 Shared Expenses | participantes; productos/servicios; asignaciones; deudas; reparto según regla aprobada; redondeo según decisión aceptada; atomicidad; ExecPlan cuando corresponda. |
| S07 Integration & Delivery | build limpio; regresión integral; trazabilidad completa; documentación coincide con código; instrucciones de build reproducibles; demo/presentación cubre pilares obligatorios. |

## 7. Puntaje diagnóstico

Para cada criterio aplicable:

`puntos = peso_del_criterio × valor(CUMPLE=1, PARCIAL=0.5, NO_CUMPLE=0)`

El Auditor puede distribuir el peso de cada dimensión entre sus subcriterios de forma uniforme o justificar una distribución distinta si el Task Brief hace un criterio claramente más relevante.

Referencia diagnóstica:
- **90–100:** calidad alta para el alcance.
- **80–89.9:** aceptable con observaciones.
- **<80:** requiere correcciones.

Estas bandas **no sustituyen los gates críticos**. Un 95 con un requisito obligatorio incumplido sigue siendo `FAIL`.

## 8. Regla de veredicto

### PASS
- todos los gates críticos cumplen;
- no hay BLOCKER/MAJOR abiertos;
- evidencia suficiente;
- no hay correcciones obligatorias pendientes.

### PASS WITH OBSERVATIONS
- todos los gates críticos cumplen;
- no hay BLOCKER/MAJOR abiertos;
- solo quedan MINOR/DOCUMENTATION no bloqueantes;
- las observaciones quedan registradas y no falsean completitud.

### FAIL
- existe al menos un gate crítico incumplido;
- o hay BLOCKER/MAJOR abierto;
- o la implementación no cumple el objetivo/criterios esenciales;
- o la calidad/evidencia es insuficiente de forma corregible.

### BLOCKED
- no puede emitirse un juicio fiable por falta de entorno, evidencia, decisión o acceso esencial.

## 9. Gates de Git

### APPROVED_FOR_COMMIT
Puede emitirse cuando el cambio está técnicamente listo para quedar registrado como commit de trabajo/aceptado según el workflow y no existe un hallazgo que lo invalide.

### APPROVED_FOR_MERGE
Requiere:
- PASS o PASS WITH OBSERVATIONS compatible;
- commit/diff exacto auditado;
- cero BLOCKER/MAJOR abiertos;
- build/tests requeridos aprobados;
- documentación necesaria coherente;
- ausencia de cambios fuera de alcance.

### APPROVED_FOR_SPRINT_ADVANCE
Requiere además:
- todas las tareas obligatorias del sprint cerrables;
- gate integral del incremento, no solo del último commit;
- trazabilidad coherente;
- documentación del sprint actualizada;
- no existen defectos conocidos que invaliden requisitos del sprint.

El Auditor autoriza técnicamente. El Orquestador formaliza el merge/cierre/avance.

## 10. Reauditoría

Después de un `FAIL`:
- revisar todos los hallazgos obligatorios;
- volver a evaluar filas afectadas;
- verificar regresiones relacionadas;
- no reabrir criterios no afectados sin una razón concreta;
- emitir un nuevo veredicto referenciando el nuevo commit/diff.

La auditoría original debe conservarse como evidencia histórica.
