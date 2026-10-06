# Auditorías de LocalCash

Este directorio guarda evidencia de revisión independiente.

## Organización recomendada

```text
docs/audits/
├── S00/
│   ├── S00-T01-audit.md
│   └── S00-final-audit.md
├── S01/
└── ...
```

Las carpetas se crean cuando exista la primera auditoría correspondiente.

## Regla

Una auditoría debe referenciar el commit, diff o estado exacto revisado. No auditar una descripción del Codificador como sustituto del código.

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

- alcance;
- requisitos/criterios revisados;
- commit/diff;
- build/tests considerados;
- hallazgos;
- veredicto;
- correcciones requeridas;
- riesgos residuales.

El Auditor no debe modificar el código durante la auditoría normal.