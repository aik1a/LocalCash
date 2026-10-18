# S01-T01 — Divisas

## Objetivo
Configuración inicial SQLite y moneda fija en cuentas y compras; precisión dependiente de moneda.

## Requisitos relacionados
Ampliación expresamente solicitada por Aikia sobre cuentas, movimientos, compras y reparto
académicos. Ver docs/tasks/active/S01-T01-divisas.md. No cambia encargo docente.

## Estado inicial
main 171aacfb: SQLite v4; Dinero convierte a centésimas y siempre muestra dos decimales.
Cuenta/Compra carecen de divisa. Configuración inicial inexistente.

## Alcance / fuera de alcance
10 divisas, configuración, migración, formularios, mapas y reparto. Sin tipos de cuenta,
tipos de cambio, transferencias, backend, nuevas dependencias o cambio biométrico.

## Diseño propuesto
Divisa enum con precisión/unidad y nombres; enteros de centésimas conservados para evitar
reescalar datos antiguos. Dinero acepta precisión válida y formatea sin redondeo.
ConfiguracionRepository con fila única. columnas divisa nullable para migración antigua,
CHECK del catálogo y asignación única validada dentro de transacción por repositorio.
Sin moneda: leer importe exacto con etiqueta pendiente; asignar antes de escribir.
ActividadProtegida pide configuración después de autenticar y antes de crear contenido.
Selectores XML reutilizados; moneda de compra bloqueada mientras tenga ítems para evitar
reinterpretarlos. Cuenta y compra antiguas solicitan asignación al abrirse.

## Plan de implementación
1. Dominio y migración v5; repositorios y servicios.
2. Configuración inicial, selectores y formatos en todas las pantallas monetarias.
3. Pruebas dominio/migración/regresión y build mediante CI si falta SDK local.
4. Revisar diff, documentar y crear PR sin merge.

## Validación
bash tests/domain-checks.sh; tests de Divisa y reparto; fixtures migraciones v1–v4 con
snapshot antes/después, errores y transacciones; assembleDebug/AndroidTest en CI API24/36.
Flujo: biometría → elección → cuenta CLP → ingreso entero → reinicio; cuenta USD distinta;
compra CLP 100/3 → 34/33/33; fracciones CLP rechazadas; rotación y migración antigua.

## Progreso
- [x] Inspección y autorización
- [x] Implementación
- [ ] Pruebas y revisión
- [ ] Handoff y PR

## Decisiones y descubrimientos
Se preserva escala de 100 unidades persistidas por unidad monetaria, también CLP/JPY.
Se rechaza exceso de precisión; no se redondea dinero del usuario silenciosamente.
Selección principal afecta valores iniciales de formularios, nunca registros existentes.

## Resultado final
En curso.

## Ampliación de alcance autorizada
Usuario pide «Tú + un contacto», retirar IDs visibles y aclaraciones técnicas.
La cuota propia se persiste en reparto_usuario(compra_id,monto), sin contacto sintético
ni deuda del usuario consigo mismo. Repartos históricos no se recalculan.

## Verificación durante implementación
34.400 checks de dominio y 55 de SQLite host PASS. Build app/AndroidTest PASS API24 y
API36 sobre 922e2d34. API24: 76 checks nativos SQLite/repositories, biometría y mapas PASS.
Smoke UI falló por un gesto del test fuera de una pantalla 320×640, no por pérdida de
ítems: XML confirma Pan CLP100 y total CLP100. Se corrige scroll con bounds reales y
se repite el recorrido. Workflow de divisas evita repetir la regresión biométrica que
ya ejecuta el workflow heredado de cada PR; mantiene pruebas nuevas, mapas y UI.

## Corrección de infraestructura y simplificación del gate
El runner API36 del workflow adicional no inició: FATAL Not enough space to create
userdata partition (7210 MB disponibles, 7373 MB requeridos). El workflow heredado
sí libera espacio y registra PASS de biometría, cuentas, compras, reparto, GPS y
reapertura API36 sobre 922e2d34 antes de la cancelación administrativa del job.
Se elimina el workflow duplicado y se agregan checks nativos de divisas, SQL host,
mapas y smoke de monedas al workflow existente s00-t06c-build.yml. No cambia app,
dependencias, AVD ni seguridad. Una matriz API24/API36 ejecuta el gate completo.
