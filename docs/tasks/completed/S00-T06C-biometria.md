# Task Brief — S00-T06C: acceso biométrico

**Sprint:** S00 (subtarea T06) · **Rol:** Codificador · **Estado:** CLOSED — 2026-10-16 (PASS WITH OBSERVATIONS / APPROVED_FOR_MERGE).
**Rama autorizada:** `task/S00-T06C-biometria`, desde `main` con T06A ya integrada (PR #6).
**Objetivo:** impedir consultar pantallas/datos financieros hasta autenticación exitosa mediante `BiometricPrompt`, con errores y cancelaciones controlados.

## Contexto y requisitos

- `LocalCash Proyecto Android.pdf`, pilar biometría; `LC-P03`, `LC-AUTH-01`, `LC-TECH-02`.
- Leer `PROJECT_ROUTER.md`, `AGENTS.md`, `PROJECT_STATE.md`, rol `CODER.md`, `ARCHITECTURE.md`, `docs/requirements/PROJECT_SPEC.md`, `docs/sprints/S02-security.md`, código real de `MainActivity`, demás Activities de datos y pruebas instrumentadas existentes.
- Respetar SDK declarado (actualmente minSdk24 / targetSdk36); la forma compatible de `BiometricPrompt` y dependencia de compatibilidad, si fuera estrictamente necesaria, debe justificarse en el handoff. No cambiar minSdk silenciosamente.

## Alcance autorizado

1. Comprobar disponibilidad de biometría enrolada y presentar desafío antes de renderizar datos privados, incluido ingreso inicial y regreso desde background.
2. Asegurar que acceso directo o intent a Activities financieras no sirva para eludir el gate; sin estado persistente de «autenticado» que sobreviva indebidamente reinicio del proceso.
3. Cancelación, bloqueo temporal o sensor sin configurar: mantener contenido protegido, mostrar indicación controlada, permitir reintentar cuando sea viable sin crash. No guardar huellas, plantillas o credenciales.
4. Usar `BiometricPrompt` (plataforma/compatibilidad Android apropiada); evitar implementar autenticación propia, backend o permisos superfluos.
5. Adaptar pruebas de emulador **sin crear un bypass productivo**; incluir éxito, cancelación y reentrada; probar interacción con todos los módulos previos y ubicación/mapa.
6. Documentación técnica mínima, CI, APK y handoff.

## Fuera de alcance

- PIN propio, cuenta remota, login Firebase, registro de usuario/contraseña, almacenamiento de huellas, seguridad financiera de producción, rediseño de todas las pantallas, funcionalidades extra, firmas APK heredadas de T03–T05 (T07).

## Criterios de aceptación

- [ ] Ningún dato de cuentas, movimientos, contactos, compras ni deudas se muestra sin éxito biométrico.
- [ ] Éxito permite navegar; cancelar/fallo bloquea y no expone contenido, también tras reabrir o acceder directamente a pantallas.
- [ ] Dispositivo sin biometría registrada muestra estado claro y no expone contenido; no se introduce acceso abierto como fallback silencioso.
- [ ] `BiometricPrompt` es la API de autenticación; ninguna huella en SQLite.
- [ ] Compatible con el rango Android declarado o limitación/decisión aprobada documentada, sin rebajar seguridad.
- [ ] Build SHA exacto PASS y pruebas instrumentadas de éxito/cancelación/bloqueo más regresión T03–T06B, sin uso de supuestos no verificables.
- [ ] Handoff con evidencia y una única solicitud de auditoría al finalizar.

## Decisiones de compatibilidad autorizadas por el Orquestador

- Compatibilidad real con **minSdk 24**: usar `androidx.biometric:biometric:1.1.0` en Java/Groovy como **única dependencia directa nueva permitida si se requiere**, respaldada por [Google Android Developers](https://developer.android.com/jetpack/androidx/releases/biometric). Mantener la versión de SDK y Gradle existente salvo bloqueo técnico documentado y escalado; no cambiar a Compose/Kotlin, no servidor.
- Su `BiometricPrompt` recibe un `FragmentActivity` y dispone de soporte compatible anterior a API 28; adaptar únicamente las Activities necesarias para proteger la app, sin refactor integral. Véase [referencia API](https://developer.android.com/reference/androidx/biometric/BiometricPrompt).
- De forma predeterminada **solo autenticación biométrica** (no inventar PIN propio, login ni bypass). Sin biometría registrada/disponible, pantalla bloqueada con aviso claro; las excepciones de compatibilidad o posibilidad de fallback deben escalarse al Orquestador si impiden cumplir un requisito normativo.
- Los datos deben mantenerse ocultos hasta la autenticación, incluyendo reentrada tras background y rutas internas. Los tests UI previos de T03–T06A se adaptarán al gate **sin desactivarlo en builds normales ni tests**; se exige evidencia de desbloqueo por desafío del emulador, estado sin registrar, cancelación, repetición tras reinicio y vuelta del background.
- Mantener SQLite v4, ubicación optativa, todas las tablas, deudas y permisos anteriores; no introducir modificaciones funcionales a movimientos o mapas.
- Si surge bloqueo de emulador para enrolamiento de huellas, documentar causa y alternativa de validación real. No declarar PASS sin autenticación verificada. Ejecutar SHA exacto, log, exit-code, APK y regresiones necesarias.

## Cierre administrativo — 2026-10-16

- **SHA funcional:** `0f79e6c57be54144b1a756b3aff3a9644b025ca8`.
- **CI original:** [run 38625086287](https://github.com/aik1a/LocalCash/actions/runs/38625086287): Android API24/API36 build PASS exit 0, 5483 comprobaciones de dominio por job, huella emulada/enrolamiento real, error/cancelación/reintento, siete Activities, ciclo de vida y reentrada PASS. Regresión T03–T06A API36 PASS.
- **Auditoría:** [S00-T06C-audit.md](../../audits/S00/S00-T06C-audit.md): `PASS WITH OBSERVATIONS`, `APPROVED_FOR_MERGE`; H-01 documentación histórica de ARCHITECTURE no bloqueante, para T07.
- **Integración:** [PR #7](https://github.com/aik1a/LocalCash/pull/7), merge SHA `1c944cde59ed6aba8e8af2f9529b29435e40af59`.
- **Pendientes de integración final T07:** dispositivo físico, variaciones SDK no ensayadas, firma y actualización APK preservando datos, cierre H-01 documental. No se afirmó prueba física.
- **S00:** permanece `IN_PROGRESS`; T06B (mapas offline) y T07 pendientes.
