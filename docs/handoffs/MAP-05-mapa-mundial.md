# Handoff — MAP-05

**Rol saliente:** implementación de corrección  
**Fecha:** 2026-11-03  
**Rama:** fix/world-map-offline, PR #18  
**Código funcional:** d22ec1665780d8a6ac7524fed13dfc9164450900; prueba ajustada en 00f5112690b085999081a96f2517f53a4ba221db.  
**Sprint:** corrección urgente fuera del cierre histórico S00.

## Objetivo ejecutado
Eliminar el bloqueo a Santiago Centro y centrar en la ubicación actual. Usuario confirma mapa mundial, funcionamiento 100% offline y botón Mi ubicación.

## Estado
IMPLEMENTED / VALIDATING — código y compilación completos; GPS y cartografía offline comprobados, suite completa en curso. No es una aprobación independiente ni autorización de merge.

## Cambios
- osmdroid 6.1.18 conservado; adaptador oficial Mapsforge 6.1.18 para mapa mundial básico, Chile detallado y archivo regional adicional importable.
- Navegación mundial sin rectángulo territorial, zoom3–20, Mi ubicación y Ver movimientos.
- GPS foreground con permiso, fix de hasta60s de antigüedad, timeout15s y cancelación al pausar. Posición actual no se guarda como movimiento.
- Preparación/importación de mapas fuera del hilo UI, conservando el contenedor protegido y FLAG_SECURE.
- Gradle/Groovy prepara recursos en compilación; ejecución sin permiso INTERNET. No requiere Python en Android Studio.
- SQLite v7 y código financiero sin cambios. App1.2.0/versionCode4.

## Archivos principales
`MapaActivity.java`, `MapaLocalView.java`, `MapasVectoriales.java`, `UbicacionMapa.java`, layouts del mapa, `app/build.gradle`, scripts/workflow y pruebas del mapa.

## Validación
| Verificación | Resultado |
|---|---|
| Build app/instrumentación | PASS |
| JUnit y dominio/SQLite host | PASS |
| GPS actual, La Florida y París (API36 simulado) | PASS en CI 41724571305 |
| Calles La Florida / base mundial Tokio y Europa sin conexión | PASS; 1622 / 24 / 23 colores en tiles reales |
| Navegación ampliada, importación y reapertura completas | EN CURSO: CI 41725781487 |
| Dispositivo físico y actualización firmada | NO EJECUTADO |

Las primeras rondas detectaron y corrigieron configuración de Gradle y un fixture USD/CLP inválido. La infraestructura presentó descarga incompleta de SDK y falta de espacio del AVD. La ejecución posterior agotó240s: los registros permitieron localizar un bloqueo de UI por encuadre antes del layout y padding fijo mayor que la altura del mapa. El logaritmo de dimensión negativa de osmdroid genera un zoom NaN. Se corrigió esperando dimensiones positivas y usando margen adaptable. Después pasaron apertura, GPS La Florida/París y tiles reales offline. La comprobación de cámara a zoom5 se ajustó a dos píxeles de tolerancia (~0,088 grados), manteniendo rechazo de límites territoriales; su suite final sigue en curso. Ningún timeout ni ronda fallida se declara PASS.

## Límites y decisiones
- Base mundial general: no contiene todas las calles del planeta. Chile detallado está incluido; otros países requieren importar un .map local. Elección explícita del usuario frente a mapas online.
- Chile fuente682169123bytes, world3276950bytes. APK aproximado690MB; ZIP de entrega comprimido aproximadamente258MB. Preparar los datos exige internet en build, no en Android.
- Datos OSM/ODbL, URLs/hashes en assets/maps/vector/provenance.json. Nunca se descargan tiles de tile.openstreetmap.org.
- Las firmas debug de CI son distintas de las instalaciones previas. No desinstalar una app con datos para usar este APK; actualizar desde Android Studio con el certificado habitual.

## Documentación
TaskBrief MAP-05, ExecPlan MAP-05, ADR005, sustitución explícita de la limitación del ADR002 y notas README/ARCHITECTURE. No se alteran auditorías históricas.

## Próxima acción
Completar la comprobación de ejecución y registrar resultados exactos. Después, revisión independiente y aceptación del Orquestador y decisión de integración a main. Sin cierre retroactivo del sprint.

## Entrega compilada

APK fuente d22ec16: 689682171 bytes, SHA256 `675749c2f583079a27734ead51859eb84be7657bb714b5936f37952d99efbf03`. [Artefacto de compilación](https://github.com/aik1a/LocalCash/actions/runs/41724571305/artifacts/13273464039). No es una firma estable para actualizar instalaciones históricas.
