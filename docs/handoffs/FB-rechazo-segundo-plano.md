# Handoff - aviso de rechazo al volver a la app

Rol: Codificador. Fecha: 2026-11-23. Base: 736f748db92cff20c8caf24f8deca5136147d057.
Estado: PARTIAL, pendiente de build Android, recorrido y auditoria independiente.

## Cambio acotado

`LocalCashStore` registra un booleano `rechazo_UID` en las preferencias ya existentes cuando recibe el fallo de un batch. No guarda importes, documentos ni el texto de la excepcion. Lo registra incluso si esa instancia se invalido al cambiar de cuenta; otra cuenta no consulta ni confirma su aviso.

`ActividadProtegida` muestra un dialogo generico cuando esta visible, autenticada y con datos preparados. Los snapshots no duplican un dialogo abierto. Solo el boton Entendido confirma el aviso; salir, bloquear o recrear la pantalla no lo confirma. Se conserva el indicador para la siguiente apertura una vez persistidas las preferencias.

Archivos: `data/LocalCashStore.java`, `presentation/ActividadProtegida.java` bajo `app/src/main/java/com/aik1a/localcash/`.

## Evidencia y limites

- Parse sintactico con API Javac de JDK 26: PASS para ambos archivos; no comprueba tipos ni ejecuta Android.
- Revision de diferencias: solo gestion del aviso; sin cambios en saldos, pagos, reglas, configuracion Firebase o dependencias.
- Build Android y prueba manual: NOT TESTED. El intento del Orquestador esta bloqueado por Google Services 4.6.0 ausente en cache offline.
- El indicador depende de recibir el callback de fallo. No se afirma deteccion de un rechazo de escrituras restauradas si su Task original se perdio al morir el proceso. `SharedPreferences.apply` tampoco acredita una prueba de interrupcion abrupta antes de persistir.

## Recorrido pendiente

En un emulador descartable y Firebase de pruebas, provocar una escritura que las reglas rechacen: (1) primer plano, comprobar un solo dialogo; (2) offline, enviar app a segundo plano, reconectar y volver tras biometria, comprobar aviso; (3) con aviso recibido pero sin confirmar, reiniciar proceso y comprobar aviso al mismo UID; (4) cambiar a UID B y comprobar que no aparece el aviso de A; volver a A y confirmar; (5) volver a abrir y comprobar que ya no aparece, manteniendo los datos reconciliados.

La prueba debe usar reglas de desarrollo controladas, no editar datos reales ni desplegar reglas del prototipo en el proyecto del usuario. Esta entrega no cierra F6 ni autoriza distribucion.
