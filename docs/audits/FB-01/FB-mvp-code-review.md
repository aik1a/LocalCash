# Revisión independiente del código MVP Firebase

Fecha: 2026-11-23. Auditor: Codex, subagente recuperar_auditoria_t02, independiente de los Codificadores Auth/datos/UI. Alcance autorizado: revisión de fallos concretos del código integrado antes de entregar ZIP; pruebas integradas/manuales se harán después. No auditoría final de distribución.

Base: 93a20f6a0dec0589b8f149546332e7cf92eead87. Código final revisado: **a398d97b018c3a11fb06df4c05b53bbd963654fd**, rama task/FB-mvp-delivery, destino previsto feat/firebase-migration.

## Bloques revisados

| Bloque | Evidencia de inspección | Resultado para incorporar WIP |
|---|---|---|
| Google y biometría | 6ea6ab0: token CredentialManager convertido a credencial FirebaseAuth real; cancelación/error no desbloquean; biometría fuerte sigue obligatoria; INTERNET en main; JSON cliente ignorado y ausente de Git. | Conforme por inspección; Google real NOT TESTED |
| Pantallas | 0308014 integrado en c5f3953: mismas pantallas/layouts/campos/extras long; consumo LocalCashStore en vez de helper; mensajes de pendiente y captura IllegalState; mapa refresca marcadores. | Conforme para WIP, sin rediseño |
| Persistencia | 751e643 integrado en ee0d49b: cinco consultas completas por users/UID; Firestore como persistencia; memoria temporal; IDs long aleatorios positivos como string de documento para conservar dominio; helper SQLite movido fuera de main. | Conforme para integración; recorrido real pendiente |
| Dinero/pago/borrado | Dominio de importes/moneda/redondeo conservado. Pago usa batch egreso p-compraId + compra, ID estable y movimiento create-only en reglas; referencias con getAfter; uso de FieldValue.increment en contadores. No declara transacciones distribuidas equivalentes a SQL ni concurrencia probada. | Sin defecto concreto bloqueante encontrado en camino de app |
| Reparto y reglas | c775586 refuerza forma, participantes mínimos, tipo contacto/marcador y cuota propia entera/no negativa/precisión/no superior a total. Dominio valida suma y participantes individuales; reglas no validan toda suma de mapa variable. | Límite explícito; no certificar reglas ante escrituras arbitrarias |
| Bootstrap y privacidad | 8f0352c: espera cinco colecciones listas antes de crear vistas; caché no preparada muestra bloqueo, no saldo cero; observer actualiza/error y se retira al pausar; reintento reabre consultas. Sesión biométrica atada a UID, Bundle de vistas también; al cambiar UID reinicia stack Main. | Conforme tras corrección H-01 |
| CI de compilación | 2266723: checkout SHA de evento, configuración cliente demo temporal, build y JUnit con pipefail, artifacts always; omite runner SQLite heredado en PR Firebase. | Apto para obtener evidencia de compilación; APK CI es demo, no prueba Google real |

## Hallazgo corregido

**H-01 — privacidad, corregido antes de empaquetar.** En 8f0352c el AuthStateListener de ActividadProtegida solo comprobaba que el proveedor siguiera siendo Google. Ante cambio A→B con Activity visible, proveedor Google seguía válido aunque la biometría perteneciera a A; las vistas A no se ocultaban en ese callback. Se pidió corrección concreta al Codificador, sin tocar implementación desde Auditoría.

**a9c1f18** cambia la condición a `!sesion().autenticada()`, que también exige UID actual igual a uidAutenticado. Oculta las vistas y abre el gate; tras biometría, cargarDatos detecta UID distinto y reconstruye stack limpio. Verificado diff de una línea y flujo circundante; no se declara reproducción manual realizada.

## Validación real y límites

Inspeccionados logs de los Codificadores: LocalCash-google-auth-mvp-build.exit=0, BUILD SUCCESSFUL; build integrado de app/instrumentación/JUnit exitoso y XML de 29 pruebas sin fallos/errores. Corrección final a9c1f18 recompiló assembleDebug PASS según entrega del Codificador; no se repitió JUnit por cambio de una condición de UI sin dominio.

Ejecutado por Auditor: lectura de diffs completos por bloque y bootstrap, comprobación de JSON no versionado/ignorado, XML JUnit y `git diff --check 93a20f6 a398d97` exit0. Auditor no implementó cambios ni repitió build ni pruebas de dispositivos.

Google real, cancelación en dispositivo, biometría/navegación/cambio de cuenta real, offline/reinicio/reconexión financiero, reglas A/B/anónimo/entradas inválidas y dos clientes pagando: **NOT TESTED**. El prototipo F1 y sus revisiones previas no acreditan estos módulos nuevos.

Observaciones de entrega: el workflow compila con demo-localcash-build y sus APK no demuestran acceso al proyecto del usuario; compilar con su JSON para probar Google. Sus paths omiten gradlew/gradlew.bat (seguimiento menor). Reglas de reparto no garantizan sumas/participantes individuales contra escrituras arbitrarias; ese límite está declarado, no inventar aprobación de invariantes remotas.

Comprobación final a398d97: revisión adicional de 4ba12fc (texto «Cambio pendiente de sincronización», sin prometer persistencia durable antes de snapshot), guía FIREBASE-MVP y estado del proyecto. La guía distingue APK CI sintético, configuración real, reglas por desplegar y pruebas finales pendientes. No añade cambios funcionales al flujo auditado.

## Dictamen

**PASS WITH OBSERVATIONS para incorporación WIP y empaquetado del código a398d97**, con H-01 corregido. No se encontraron otras correcciones obligatorias demostradas dentro de esta inspección breve. No exige nuevas capas, rediseño ni infraestructura adicional antes de entregar el bloque de código.

**Aceptación final/distribución: pendiente; NOT_APPROVED_FOR_PHASE_ADVANCE / NOT_APPROVED_FOR_SPRINT_ADVANCE.** No se declara F1–F6 cerrada, Google ni offline comprobados, ni reglas financieras completas frente a escrituras arbitrarias. El cierre requiere las pruebas finales acordadas sobre el SHA efectivamente publicado y corrección de fallos que arrojen. Auditor no fusiona ni modifica código.
