# Referencias de ingeniería para agentes

Estas referencias orientan el proceso de trabajo, no reemplazan los requisitos del docente.

## OpenAI — How OpenAI uses Codex

Aplicación en LocalCash:
- tareas pequeñas y bien delimitadas;
- plan antes de cambios grandes;
- prompts similares a issues con archivos/componentes/criterios;
- `AGENTS.md` como contexto persistente;
- iterar sobre el entorno y usar build/tests como feedback.

Fuente oficial: https://openai.com/business/guides-and-resources/how-openai-uses-codex/

## OpenAI — Harness engineering

Aplicación en LocalCash:
- `AGENTS.md` corto como mapa, no enciclopedia;
- `docs/` como sistema de registro del repositorio;
- planes activos/completados versionados;
- progressive disclosure: el agente lee primero lo mínimo y profundiza según tarea;
- documentación verificable y mantenida junto al código.

Fuente oficial: https://openai.com/index/harness-engineering/

## OpenAI — ExecPlans / PLANS.md

Aplicación en LocalCash:
- planes vivos para trabajo complejo;
- deben ser autosuficientes respecto del repositorio;
- registrar progreso, decisiones y validación;
- usar solo cuando la complejidad lo justifique.

Fuente oficial (receta archivada, usada como patrón, no como requisito vigente): https://developers.openai.com/cookbook/articles/codex_exec_plans

## Principio adoptado: loop de ingeniería

LocalCash aplica un ciclo de feedback:

`EXPLORE -> PLAN -> IMPLEMENT -> BUILD -> TEST -> REVIEW -> FIX -> DOCUMENT`

El agente debe observar resultados reales (build, tests, diff y comportamiento) y corregir antes de declarar DONE.