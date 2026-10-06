# Operación multiagente de LocalCash

Este directorio define cómo colaboran los agentes del proyecto sin depender del historial de chats.

## Roles

- **Orquestador:** controla alcance, estado, secuencia, contexto y cierre.
- **Codificador:** implementa una tarea acotada y verifica su resultado.
- **Auditor:** revisa de forma independiente cumplimiento, calidad y evidencia.

Reglas completas:
- `roles/ORCHESTRATOR.md`
- `roles/CODER.md`
- `roles/AUDITOR.md`

## Flujo estándar

`ORQUESTADOR → TASK BRIEF → CODIFICADOR → BUILD/TEST → HANDOFF → AUDITOR → VEREDICTO → ORQUESTADOR`

Si el resultado es `FAIL`, el Orquestador genera una tarea correctiva para un Codificador. Si es `PASS`, puede autorizar la siguiente tarea.

## Documentos con funciones distintas

| Documento | Función |
|---|---|
| `PROJECT_STATE.md` | estado actual y próxima acción |
| Sprint activo | alcance y criterios del incremento |
| ExecPlan | plan vivo para trabajo complejo |
| Handoff | transferencia concreta entre agentes |
| Auditoría | evaluación independiente |
| `TRACEABILITY.md` | requisito → diseño → código → prueba |
| ADR | decisión arquitectónica y fundamento |

No mezclar estas funciones en un único documento.