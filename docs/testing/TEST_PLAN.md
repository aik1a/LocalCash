# Plan de pruebas

## Estrategia

Las pruebas se agregan por sprint, no al final. Cada requisito debe tener una evidencia asociada en `TRACEABILITY.md`.

## Niveles

- Dominio: invariantes y cálculos (saldo, subtotal, deudas).
- Persistencia: CRUD, relaciones y reconstrucción de objetos.
- Integración Android: biometría, permisos/ubicación y mapa según capacidades del entorno.
- Manual: flujos de UI que requieran dispositivo/emulador.
- Build: compilación desde estado limpio.

## Casos base

- BUILD-01: proyecto compila.
- DB-01: crear BD y CRUD base sin pérdida de integridad.
- AUTH-01: acceso protegido por biometría.
- ACC-01: crear/consultar cuenta.
- MOV-01: ingreso/egreso produce saldo esperado.
- GEO-01: movimiento con y sin ubicación.
- MAP-01: representar coordenadas persistidas.
- CON-01: contacto local persistente.
- BUY-01: compra + detalles + total.
- BUY-02: compra planificada.
- BUY-03: relación con contacto.
- SHARE-01/02: participantes/asignaciones.
- DEBT-01/02: cálculo de deuda equitativa/personalizada.
- DOC-01: docs reflejan implementación.
- DEMO-01: flujo de exposición reproducible.

## Regla de regresión

Un bug corregido debe incorporar una prueba automatizada cuando sea razonablemente testeable; si depende de UI/dispositivo, registrar escenario manual reproducible.