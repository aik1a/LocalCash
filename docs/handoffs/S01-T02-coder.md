# Handoff — S01-T02
Rol saliente: Codificador. Fecha: 2026-10-19.
Rama: task/S01-T02-pagos-crud. PR: https://github.com/aik1a/LocalCash/pull/14.
Base: 523f3bd50c58f0f827e4e1efb864f05bd8ddea09. SHA técnico: 1d6ce3b64262468081345c5e3464daeef52b8640.

## Estado
DONE (codificación): implementación y validación completas; listo para auditoría independiente. No es autorización de merge.

## Comportamiento
- Egresos y edición/borrado manual revalidan saldo dentro de transacción. Nuevas cuentas rechazan saldo inicial negativo. Datos negativos anteriores permanecen legibles y solo admiten mantenerse/mejorar; no se inventa saldo.
- Planificar no descuenta. Realizar exige elegir/confirmar una cuenta en igual divisa, total positivo y fondos suficientes. Crear realizada utiliza la misma confirmación. Estado, egreso y vínculo son atómicos; reintento sobre la misma compra/cuenta no vuelve a cobrar.
- Cuota de reparto no duplica cobro: se descuenta total de compra y reparto registra las participaciones/deudas.
- CRUD: nombres de cuentas/contactos, tipo/monto/descripcion de movimientos manuales conservando fecha/ubicación, datos/ítems de planificadas y nombre de realizadas. Saldo inicial/divisa permanecen fijos.
- Borrado confirmado: cuentas solo sin movimientos; contactos sin referencias; compras sin reparto. Borrar compra pagada revierte el egreso atómicamente. Su movimiento no se edita/elimina por separado.
- SQLite6 aditiva: pago_compra vincula compra/egreso con FK/unicidad. Realizadas antiguas no reciben cargos retroactivos.
- Interfaz muestra cuenta, importe y saldo; Toast para éxito, errores persistentes en pago/formularios; acciones de mantenimiento y estados legibles. Biometría y FLAG_SECURE preservados.

## Validación local
Dominio34.400 PASS; SQLite host77 PASS con DDL real y migraciones v1–v5→6; XML y Python válidos, diff sin errores. Compilación parcial con stubs usada por capa data no equivale a build Android.
CI final: https://github.com/aik1a/LocalCash/actions/runs/39203915078, sobre SHA técnico1d6ce3b. SUCCESS ambos jobs: build, Currency76 y Payments91 por API, biometría, mapas y UI divisas/pagos/CRUD. Regresión financiera/GPS completa PASS API36.
PaymentCrudChecks prueba fondos exactos/insuficientes/divisa, snapshots de rollback/rechazo, creación realizada, reintento, reapertura, reembolso, CRUD protegido y migración negativa legada.
currency-ui crea realizada, cancela pago y conserva borrador, luego confirma; payments-crud recorre plan→realizada, rechazo por fondos, edición de ítem, reinicio, egreso rechazado y reembolso. Regresión heredada conserva el recorrido completo financiero/GPS en API36.

## Documentación
Task Brief y ExecPlan; DATABASE, BUSINESS_RULES, DOMAIN_MODEL, ARCHITECTURE y TRACEABILITY. Son una ampliación expresamente autorizada por Aikia; no una nueva exigencia del PDF docente. No se altera auditoría/cierre S00 ni se declara cerrado roadmap S01.

## Límites y pendientes
Auditoría independiente antes de integrar. Emulador no acredita dispositivo físico, recorrido humano ni Sync limpio de Studio.
Upgrade histórico con firmantes diferentes sigue BLOCKED_SIGNER_MISMATCH; migración SQLite no acredita ese upgrade y no se debe desinstalar una instalación con datos.
Compras gratuitas pueden planificarse, pero realizar requiere total positivo. No se agregan cambio de moneda, pagos parciales ni cierre/pago de deudas.
No se han generado un nuevo paquete académico ni instalado APK en el equipo del usuario.

## Incidencias verificadas, corregidas y revalidadas
Run39200828583 API24: tap buscó Mercado sobre snapshot Main tras reinicio; helper payments ahora espera transición25s. Run39202278898 API24: misma carrera en helper currency, corregida. API36 del mismo run: egreso marcado y monto1, pero setError no expuso texto; formulario ahora muestra TextView persistente/liveRegion en registro y edición. En ambos runs nativos Currency76 y Payments91 pasaron, sin calificar esos runs globalmente PASS. La prueba FK heredada se ajustó a SQL directo pues el repository ahora valida la cuenta antes de llegar al motor; no se debilitó la aserción FK.

## Evidencia final
CI39203915078 SUCCESS API24 job117902510159 y API36 job117902510022, incluidos artefactos/APK. Artefactos12164774845 (API24) y12165865443 (API36). El commit documental posterior no modifica el SHA técnico validado. No hay fallo funcional conocido pendiente tras esta suite; no equivale a validación exhaustiva o física.
