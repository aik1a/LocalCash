# ExecPlans de LocalCash

Un ExecPlan es un documento vivo para trabajo complejo. Debe permitir que otro agente continúe la tarea usando solo el repositorio y el plan, sin depender de chats previos.

## Cuándo usarlo

Crear un ExecPlan cuando una tarea:
- afecta varios componentes o capas;
- requiere decisiones de diseño;
- necesita varias iteraciones de implementación/prueba;
- puede durar varias horas;
- tiene riesgos de alcance o regresión relevantes.

No crear uno para cambios pequeños y evidentes.

## Ubicación

- Activos: `docs/exec-plans/active/`
- Terminados: `docs/exec-plans/completed/`

## Estructura mínima

# <ID y nombre>

## Objetivo
Resultado observable que debe existir al terminar.

## Requisitos relacionados
IDs de `PROJECT_SPEC.md` y enlaces internos relevantes.

## Estado inicial
Qué existe realmente en el repositorio antes del cambio.

## Alcance
Qué se implementará.

## Fuera de alcance
Qué deliberadamente no se implementará.

## Diseño propuesto
Clases, tablas, pantallas, flujos y archivos afectados.

## Plan de implementación
Pasos pequeños, ordenados y verificables.

## Validación
Comandos de build/test y escenarios manuales con resultado esperado.

## Progreso
Checklist actualizado durante la ejecución.

## Decisiones y descubrimientos
Registrar decisiones tomadas durante el trabajo y por qué.

## Resultado final
Qué quedó implementado, pruebas ejecutadas, limitaciones y documentación actualizada.

## Regla de ejecución

El plan no es una predicción rígida. Si el código revela una diferencia, actualizar primero el plan y luego continuar. Nunca ocultar divergencias entre plan, documentación y repositorio.