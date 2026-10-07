# Auditorías de LocalCash

Este directorio guarda el marco canónico y la evidencia de revisión independiente.

## Instrumentos canónicos

- `AUDIT_MATRIX.md`: matriz de calidad, gates, pesos, focos por sprint y reglas de veredicto.
- `AUDIT_TEMPLATE.md`: plantilla obligatoria para informes de auditoría.

## Organización recomendada

```text
docs/audits/
├── AUDIT_MATRIX.md
├── AUDIT_TEMPLATE.md
├── S00/
│   ├── S00-T01-audit.md
│   └── S00-final-audit.md
├── S01/
└── ...
```

Las carpetas de sprint se crean cuando exista la primera auditoría correspondiente.

## Regla

Una auditoría debe referenciar el commit, diff o estado exacto revisado. No auditar una descripción del Codificador como sustituto del código.

Toda auditoría normal debe usar la matriz base Q01–Q08. Los criterios no aplicables se marcan `N/A`; no se omiten silenciosamente.

El puntaje es diagnóstico. Los gates críticos y severidades prevalecen sobre el puntaje.

## Veredictos

- `PASS`
- `PASS WITH OBSERVATIONS`
- `FAIL`
- `BLOCKED`

## Severidades

- `BLOCKER`
- `MAJOR`
- `MINOR`
- `DOCUMENTATION`
- `OUT-OF-SCOPE`

## Contenido mínimo

- alcance e identificación exacta del estado auditado;
- fuentes consultadas;
- criterios de aceptación;
- matriz de calidad;
- evidencia de build/tests/manual;
- hallazgos;
- gates críticos;
- veredicto;
- autorizaciones;
- correcciones requeridas;
- observaciones no bloqueantes;
- riesgos residuales.

El Auditor no debe modificar el código durante la auditoría normal.
