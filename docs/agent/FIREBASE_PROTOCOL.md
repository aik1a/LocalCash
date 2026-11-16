# Protocolo operativo y de auditoría — FB-01

Vigente desde 2026-11-16. Aprobación de alcance: usuario solicita publicar el roadmap, establecer ramas y fijar protocolos. Complementa WORKFLOW.md y TASK_LIFECYCLE.md; prevalece para los detalles específicos de FB-01. No sustituye roles ni permite autoauditoría de código.

## Ramas y conservación de la base

- Etiqueta `baseline/sqlite-v7-20261116`: commit c28a27b87aeeb092a936b8ba6b05b83404ac1c13. Identifica una base reproducible, no certifica pruebas que no se hayan ejecutado ni una firma APK compatible.
- `docs/firebase-roadmap`: documentación de arranque hacia main; no contiene código funcional.
- `feat/firebase-migration`: rama de integración creada desde main después de integrar esta documentación. Es trabajo en progreso, no una versión apta para distribuir.
- `task/FB-Fx-Tyy-descripcion`: rama de tarea desde el HEAD aceptado de integración; PR dirigido a feat/firebase-migration.
- `main`: mantiene la app SQLite durante la migración. Recibe documentación operativa y, al terminar, el incremento Firebase completo tras F6.
- No force-push sobre ramas compartidas, no mover la etiqueta base y no fusionar cambios funcionales parciales directamente a main. Ante avances de main, incorporar sus cambios de manera explícita y reevaluar pruebas afectadas.

## Calidad MVP y proporcionalidad

Instrucción explícita del usuario, 2026-11-16: evitar sobreingeniería; calidad de MVP académico de alumno. Este criterio regula el tamaño de tareas, soluciones, documentación y auditorías de todo FB-01.

- Priorizar código Java legible, reutilizar dominio/pantallas existentes y los SDK oficiales. Introducir clases o abstracciones solo cuando resuelvan una necesidad concreta de la tarea.
- No crear frameworks propios de repositorios, motores de sincronización/conflictos, colas persistentes paralelas, backend propio ni capas genéricas para posibles necesidades futuras. Aprovechar primero la persistencia y sincronización del SDK Firestore.
- Demostrar un flujo pequeño completo antes de extenderlo a otros módulos. Requisitos obligatorios y reglas financieras existentes siguen siendo criterios de aceptación.
- Documentar brevemente decisiones, evidencia y límites; no generar informes duplicados ni auditorías por cada commit. Una revisión independiente por tarea funcional y un gate final integrado son suficientes, con reevaluación focalizada de correcciones.
- Auditorías y pruebas proporcionales: centrarse en funcionamiento, datos, privacidad entre cuentas y regresiones del cambio. No exigir infraestructura de producción, certificaciones, pruebas de carga ni cobertura exhaustiva sin requisito concreto.
- Dos clientes simultáneos se prueban de forma acotada donde afecten doble pago o integridad. No asumir edición colaborativa ni sincronización avanzada como funcionalidad del MVP. Si cumplir una invariante exige complejidad adicional, registrar el problema y decidir la alternativa mínima antes de construirla.
- Una observación de estilo o mejora futura no bloquea una tarea que cumple sus criterios. Integridad monetaria, conservación de datos y aislamiento de usuarios sí deben resolverse.

## Roles y unidad de trabajo

Orquestador prepara el brief, resuelve dependencias, registra decisiones, actualiza estado y formaliza merge/cierre. Codificador implementa el alcance y aporta evidencia. Auditor independiente revisa el commit exacto, no corrige el código ni amplía la tarea. No basta cambiar el nombre del rol dentro del mismo chat para obtener independencia.

Una tarea debe producir un resultado verificable y cubrir una parte del flujo; nunca asignar la migración completa como una única tarea. Al comenzar registrar base SHA, rama destino, requisitos, archivos previstos y pruebas. Si surge una decisión funcional no definida, documentarla y devolverla al Orquestador antes de implementar la parte dependiente; continuar lo independiente cuando sea posible.

La creación de ramas y tareas no despacha agentes automáticamente. El Orquestador proporciona el brief para ejecución posterior con el rol adecuado.

## Límites de implementación

- Java, XML, dominio financiero y representación monetaria entera continúan vigentes. No agregar Room, Compose, DI, otro backend ni dependencias principales ajenas al plan.
- No modificar reglas de pagos, redondeo, monedas, deudas o borrado para acomodar Firebase sin decisión documentada. No sustituir biometría por Google.
- Contactos no son cuentas colaboradoras; no habilitar acceso compartido, notificaciones ni transferencias reales.
- SQLite puede leerse para importación durante la transición; no crear doble escritura SQLite/Firestore como arquitectura final.
- Firebase de desarrollo/emuladores y datos sintéticos por defecto. No subir bases reales, coordenadas, correos o respaldos personales a pruebas, logs, repositorio ni herramientas de auditoría.
- No publicar credenciales privadas, service-account JSON, tokens OAuth, claves de firma ni contraseñas. google-services.json es configuración cliente, no una credencial administrativa; decidir su distribución y restringir las claves según la configuración del proyecto.
- Crear un proyecto de desarrollo y habilitar Google/Firestore forma parte de F2 cuando se conozca la cuenta/proyecto responsable. Facturación, cambios destructivos, datos reales o distribución de producción requieren alcance explícito; no inferirlos del permiso para planificar.
- No borrar SQLite original ni cerrar sesión descartando pendientes para facilitar una prueba. No desinstalar una app con datos para resolver firma incompatible.
- No afirmar disponibilidad offline total sin comprobar preparación, cobertura y límites de caché; un saldo con historial parcial no es un saldo definitivo.

## Auditoría por tarea

Entradas obligatorias: brief, base SHA, head SHA, diff completo, contexto pertinente, decisiones aceptadas, handoff y evidencia de pruebas. El Auditor inspecciona implementación real y dependencias; no se limita al resumen del Codificador.

Cada informe en docs/audits/FB-01/ identifica:

1. Tarea, autor de implementación, auditor, fecha, base/head y rama destino.
2. Requisitos/criterios contrastados, hallazgos con archivo/línea o escenario reproducible.
3. Pruebas inspeccionadas y pruebas repetidas por el Auditor, claramente diferenciadas; entorno, resultado y limitaciones.
4. Severidad, veredicto y autorización concreta. No usar una puntuación numérica como sustituto de los criterios.

| Severidad | Ejemplos y consecuencia |
|---|---|
| BLOCKER | Acceso entre usuarios, pérdida de datos, doble cargo, corrupción monetaria, secreto administrativo publicado: impide merge y avance |
| MAJOR | Requisito/criterio obligatorio incumplido, fallo offline/reinicio, rechazo remoto oculto, cobertura crítica ausente: impide merge y avance |
| MINOR | Defecto no crítico con impacto limitado y alternativa clara: puede quedar con responsable y tarea de seguimiento |
| OBSERVATION | Mejora opcional o límite conocido que no incumple aceptación: no obliga a ampliar alcance |

PASS: criterios y evidencia completos. PASS WITH OBSERVATIONS: criterios obligatorios completos, solo MINOR/observaciones con seguimiento explícito. FAIL: defecto o criterio incumplido. BLOCKED: falta decisión, entorno o evidencia imprescindible; no equivale a PASS.

El informe debe decir APPROVED_FOR_MERGE o NOT_APPROVED_FOR_MERGE e identificar SHA y destino. Para cerrar fase: APPROVED_FOR_PHASE_ADVANCE o NOT_APPROVED_FOR_PHASE_ADVANCE; corresponde al gate APPROVED_FOR_SPRINT_ADVANCE del workflow general. No cerrar por promedio de pruebas si falla un criterio crítico.

Tras correcciones, el Auditor revisa hallazgos y regresión afectada sobre el nuevo SHA. Un cambio funcional posterior invalida la autorización anterior hasta reevaluar su impacto; no es obligatorio repetir toda la batería por una corrección editorial. El Orquestador comprueba que no se fusiona un SHA distinto del revisado.

## Validación proporcional

| Cambio | Evidencia mínima |
|---|---|
| Orquestación documental sin código | Diff, enlaces, coherencia de requisitos/estado y registro de publicación; no build Android ni auditoría funcional ficticia |
| Dominio o mapeo | Unitarias de invariantes, IDs, importes y compatibilidad de serialización; build si afecta código compilable |
| Reglas/consultas | Emulator Suite: propietario, A/B, anónimo, campos inválidos, referencias e intentos de violar invariantes; índices documentados |
| Repositorios/servicios | Integración online/offline, reintento, error y reinicio para las operaciones afectadas; build Android |
| Auth/sesión/UI | Google real además del emulador; cancelación, navegación, biometría, segundo plano, cambio de cuenta y datos pendientes |
| Pago/reparto/borrado | Atomicidad, duplicados, rechazos y concurrencia de dos clientes; todas las garantías financieras afectadas |
| Importación/actualización | Copia sintética representativa, conteos/relaciones/saldos, interrupción/reanudación, confirmación remota y firma compatible |

Guardar SHA, comandos, entorno/dispositivo, códigos de salida y resultados. Un build verde no acredita flujos manuales, reglas de acceso ni pruebas físicas. NOT TESTED es obligatorio cuando no se ejecutó una verificación; si era criterio obligatorio, bloquea el cierre técnico. Revisar los filtros y referencias de CI: los workflows históricos por tarea no garantizan ejecución sobre las nuevas ramas.

## Gate final y recuperación

Antes de PR Firebase hacia main: F0–F5 cerradas, F6 completo, revisión independiente del incremento integrado, matriz FB-R01–05 con evidencia, cero BLOCKER/MAJOR, pendientes menores aceptados con seguimiento, APK firmado y prueba de actualización/migración aplicable. La aceptación debe demostrar modo avión, reinicio, reconexión, aislamiento y ausencia de doble pago.

Volver al código de la etiqueta base no implica que un APK antiguo pueda abrir datos nuevos ni conservar operaciones creadas en Firebase. Ante falla: detener distribución, conservar datos y evidencia, identificar versión/esquema afectado y preparar corrección o exportación validada. No prometer downgrade ni hacer borrados/reversiones de datos automáticos.

## Publicación documental de arranque

El usuario autoriza publicar e integrar esta planificación. Se aplica la excepción existente de TASK_LIFECYCLE.md para orquestación documental sin cambios funcionales. La revisión de diff/enlaces de esta entrega es control documental del Orquestador; no se presenta como auditoría técnica independiente de Firebase. Los gates funcionales anteriores rigen desde la primera implementación.
