# Task Brief preliminar — S00-T06C: acceso biométrico

**Sprint:** S00 (subtarea T06) · **Rol:** Codificador · **Estado:** DRAFT (orden previsto después de mapas; puede adelantarse si T06B queda bloqueada).
**Rama futura:** `task/S00-T06C-biometria`, desde `main` aceptado al inicio.
**Objetivo:** impedir consultar pantallas/datos financieros hasta autenticación exitosa mediante `BiometricPrompt`, con errores y cancelaciones controlados.

## Contexto y requisitos

- `LocalCash Proyecto Android.pdf`, pilar biometría; `LC-P03`, `LC-AUTH-01`, `LC-TECH-02`.
- Leer `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, rol `CODER.md`, `ARCHITECTURE.md`, `docs/requirements/PROJECT_SPEC.md`, `docs/sprints/S02-security.md`, código real de `MainActivity`, demás Activities de datos y pruebas instrumentadas existentes.
- Respetar SDK declarado (actualmente minSdk24 / targetSdk36); la forma compatible de `BiometricPrompt` y dependencia de compatibilidad, si fuera estrictamente necesaria, debe justificarse en el handoff. No cambiar minSdk silenciosamente.

## Alcance previsto

1. Comprobar disponibilidad de biometría enrolada y presentar desafío antes de renderizar datos privados, incluido ingreso inicial y regreso desde background.
2. Asegurar que acceso directo o intent a Activities financieras no sirva para eludir el gate; sin estado persistente de «autenticado» que sobreviva indebidamente reinicio del proceso.
3. Cancelación, bloqueo temporal o sensor sin configurar: mantener contenido protegido, mostrar indicación controlada, permitir reintentar cuando sea viable sin crash. No guardar huellas, plantillas o credenciales.
4. Usar `BiometricPrompt` (plataforma/compatibilidad Android apropiada); evitar implementar autenticación propia, backend o permisos superfluos.
5. Adaptar pruebas de emulador **sin crear un bypass productivo**; incluir éxito, cancelación y reentrada; probar interacción con todos los módulos previos y ubicación/mapa.
6. Documentación técnica mínima, CI, APK y handoff.

## Fuera de alcance

- PIN propio, cuenta remota, login Firebase, registro de usuario/contraseña, almacenamiento de huellas, seguridad financiera de producción, rediseño de todas las pantallas, funcionalidades extra, firmas APK heredadas de T03–T05 (T07).

## Criterios de aceptación previstos

- [ ] Ningún dato de cuentas, movimientos, contactos, compras ni deudas se muestra sin éxito biométrico.
- [ ] Éxito permite navegar; cancelar/fallo bloquea y no expone contenido, también tras reabrir o acceder directamente a pantallas.
- [ ] Dispositivo sin biometría registrada muestra estado claro y no expone contenido; no se introduce acceso abierto como fallback silencioso.
- [ ] `BiometricPrompt` es la API de autenticación; ninguna huella en SQLite.
- [ ] Compatible con el rango Android declarado o limitación/decisión aprobada documentada, sin rebajar seguridad.
- [ ] Build SHA exacto PASS y pruebas instrumentadas de éxito/cancelación/bloqueo más regresión T03–T06B, sin uso de supuestos no verificables.
- [ ] Handoff con evidencia y una única solicitud de auditoría al finalizar.
