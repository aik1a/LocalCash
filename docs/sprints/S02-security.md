# S02 — Security / Biometric Access

## Objetivo

Bloquear el acceso al contenido financiero hasta autenticar correctamente mediante `BiometricPrompt`.

## Tareas

- S02-T01: comprobar disponibilidad/capacidad biométrica.
- S02-T02: implementar flujo de autenticación.
- S02-T03: manejar éxito, cancelación y error de forma controlada.
- S02-T04: verificar que no se persistan datos biométricos propios.
- S02-T05: pruebas manuales/automatizables y documentación.

## Criterios de aceptación

- Sin autenticación válida no se accede al contenido protegido.
- El flujo usa API Android y no almacena huellas.
- Errores/cancelación no producen crash.