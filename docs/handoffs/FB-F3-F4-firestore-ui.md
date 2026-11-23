# FB-F3/F4 — Adaptación de pantallas a Firestore

Base: 93a20f6. Rama: task/FB-F3-F4-firestore-ui. Rol: Codificador UI.
Estado: código preparado para integración; no aceptación funcional final.

- Las ocho pantallas financieras y DivisasUI consumen LocalCashStore.get(Context)
  y los repositorios/servicios existentes, sin crear ni abrir SQLite.
- Se conserva Java/XML, navegación, formularios, IDs long, formatos y cálculos de
  dominio. No se cambian layouts ni se importan datos históricos.
- Lecturas síncronas son exclusivamente de la vista en memoria preparada por
  LocalCashStore. La actividad protegida debe esperar bootstrap asíncrono completo
  y volver a invocar actualizarContenido mediante el listener del store. No hay
  llamadas de red bloqueantes ni Tasks.await en estas pantallas.
- Los éxitos indican sincronización pendiente cuando el store ya encoló cambios.
  Los rechazos remotos se comunican desde el gate/listener compartido y deben
  refrescar la vista; mapa y reparto ahora actualizan marcadores/deudas sin
  reconstruir el formulario. Un reparto revertido permite reintentar sin perder
  los importes escritos.

Contrato acordado con Codificador data: LocalCashStore implementa AutoCloseable,
close no-op, get(Context), estaListo(), hayPendientes() y listeners alCambiar/alError.
Repositorios y tres servicios conservan sus métodos y reciben LocalCashStore.
Auth posee ActividadProtegida/AccesoActivity/LocalCashApplication y su integración
del gate; este cambio no modifica esos archivos.

Verificado: diff y alcance; cero referencias SQLite en los diez archivos de este
cambio; git diff --check correcto. Compilación aislada: NOT TESTED, depende de la
implementación de data y del gate Auth aún en ramas separadas. El Orquestador
compilará el conjunto integrado. Google real, offline, reinicio, sincronización,
reglas y recorrido visual: NOT TESTED; se validan al final conforme al usuario.
