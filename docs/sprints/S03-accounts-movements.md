# S03 — Accounts & Movements

## Objetivo

Permitir gestionar cuentas y registrar ingresos/egresos con saldo consistente.

## Tareas

- CRUD de cuentas requerido por el flujo.
- Registrar ingreso.
- Registrar egreso.
- Listar movimientos por cuenta.
- Calcular saldo desde saldo inicial + movimientos.
- Validar montos/tipos mediante dominio.
- Integrar UI sin SQL ni reglas contables en Activities.
- Pruebas y trazabilidad.

## Criterios de aceptación

- Múltiples cuentas funcionales.
- Ingreso aumenta saldo y egreso lo disminuye.
- No existe `setSaldoActual` como vía para romper consistencia.
- Persistencia sobrevive reinicio de app/proceso según prueba disponible.