# Task Brief — S00-T07C: documentación y paquete de entrega

**Sprint:** S00 · **Rol:** Codificador · **Estado:** PLANNED (habilitar tras T07A y usar resultados T07B) · **Rama prevista:** `task/S00-T07C-documentacion-entrega`, desde main aceptado tras T07A/T07B.

## Objetivo
Entregar documentación técnica y académica coherente con el código y evidencia, sin reescribir hechos históricos.

## Requisitos relacionados
LC-TECH-04/05/06 y trazabilidad de todos los requisitos LC-P01–P04.

## Context Pack
PROJECT_ROUTER.md, AGENTS.md, PROJECT_STATE.md, docs/agent/roles/CODER.md, docs/requirements/PROJECT_SPEC.md, docs/requirements/TRACEABILITY.md, docs/sprints/S00-foundation.md, docs/audits/S00/S00-T06B-audit.md, ARCHITECTURE.md, BUILD.md, docs/testing/S00-T07A.md y docs/testing/S00-T07B.md; fuente normativa PDF docente.

## Alcance
1. Resolver H-01: corregir ARCHITECTURE.md (ocho rutas protegidas y osmdroid), BUILD.md (dependencias y procedimiento vigente); conservar hitos y advertencias históricas con fecha.
2. Revisar TRACEABILITY.md frente a implementación, pruebas, decisiones y PDF docente. Identificar limitaciones sin inventar cumplimiento.
3. Preparar instrucciones reproducibles de checkout, Android Studio/Gradle, instalación limpia, uso offline, cobertura del mapa, requisitos biométricos y restricciones de firma/upgrade.
4. Preparar material de demostración breve por flujo funcional, APK final identificado por SHA, enlaces a evidencias CI y checklist de entrega exigida. No incluir secretos ni datos reales.
5. Registrar dispositivo físico como PASS únicamente si existe prueba verificable; de otro modo NOT TESTED.
6. Crear `docs/delivery/S00-final-delivery.md`, `docs/handoffs/S00-T07C-coder.md` y anexos mínimos pertinentes.

## Fuera de alcance
Cambios de requisitos/ADR, nuevas funcionalidades, pruebas ficticias, rediseño de arquitectura, merge, cierre S00 o auditoría propia.

## Criterios de aceptación
- [ ] ARCHITECTURE.md y BUILD.md reflejan el estado real, con historia conservada.
- [ ] Matriz de trazabilidad y requisitos docentes revisados.
- [ ] Instrucciones de instalación/demostración aplicables y limitaciones de firma/AVD/físico transparentes.
- [ ] Paquete académico verificable y APK/hash/evidencia enlazados.
- [ ] Handoff documental completo.

## Validación
Comprobar rutas/enlaces, instrucciones contra código/CI, consistencia de hashes y revisión documental.

## Entrega
Commit(s) documentales, paquete y handoff para auditoría independiente.
