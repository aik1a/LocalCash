# Arquitectura de LocalCash

**Estado:** aprobada como arquitectura base; detalles físicos de BD sujetos a cierre en `docs/design/DATABASE.md`.

## Objetivo arquitectónico

Mantener una aplicación Android Java sencilla, demostrable y mantenible, con responsabilidades separadas sin introducir una arquitectura empresarial innecesaria.

## Capas

```text
Presentation
Activities / Fragments / XML / RecyclerView
        |
        v
Application / Services
(coordinación de operaciones cuando sea necesaria)
        |
        v
Domain
(entidades, value objects, invariantes y comportamiento)
        |
        v
Repositories
(CRUD y consultas de persistencia)
        |
        v
SQLiteOpenHelper
        |
        v
SQLite
```

Componentes transversales:

```text
BiometricPrompt -> controla acceso a la aplicación
Android Location -> Ubicacion -> Movimiento -> osmdroid
```

## Dependencias permitidas

| Desde | Hacia | Regla |
|---|---|---|
| UI | Services/Domain | Permitido según flujo |
| UI | Repository | Evitar; usar lógica de aplicación cuando corresponda |
| UI | SQLite | Prohibido |
| Service | Domain | Permitido |
| Service | Repository | Permitido |
| Domain | SQLite | Prohibido |
| Domain | UI Android | Prohibido |
| Repository | SQLiteOpenHelper | Permitido |

## Presentación

Responsabilidades:
- renderizar información;
- capturar interacción del usuario;
- validación superficial de formulario;
- invocar operaciones de aplicación/dominio;
- mostrar errores/resultados.

No debe ejecutar SQL, calcular saldos/deudas ni modificar internamente entidades saltándose su API pública.

## Dominio y POO

El encapsulamiento no se considera satisfecho solo por usar `private`. Las entidades protegen invariantes y exponen operaciones con significado de negocio.

Ejemplos esperados:
- `Cuenta.calcularSaldo(...)`
- `Movimiento.getMontoConSigno()`
- `Movimiento.asignarUbicacion(...)`
- `Compra.calcularTotal()`
- `Deuda.marcarComoPagada()`

Evitar setters genéricos como `setSaldo`, `setTotal` o `setEstado` cuando permitan saltarse reglas.

## Services

Crear Services solo si coordinan varias entidades/repositorios o una operación de negocio que no pertenece naturalmente a una sola entidad. No crear un Service por cada clase por simetría.

Candidatos actuales:
- `MovimientoService`
- `CompraService`
- `DeudaService`

Su necesidad exacta debe confirmarse durante implementación.

## Repositories

Centralizan persistencia y mapeo entre SQLite y objetos Java. SQL no debe dispersarse en Activities/Fragments.

Candidatos:
- `CuentaRepository`
- `MovimientoRepository`
- `ContactoRepository`
- `CompraRepository`
- `DeudaRepository`

## SQLite

Un `LocalCashDbHelper` basado en `SQLiteOpenHelper` administra creación y versión de la base. No contiene reglas de negocio.

SQLite es la única fuente persistente de verdad en esta versión. Véase `docs/design/decisions/ADR-001-local-sqlite.md`.

## Biometría

`BiometricPrompt` autentica mediante las capacidades del dispositivo. LocalCash no almacena huellas ni plantillas biométricas en SQLite.

## Mapas y ubicación

La ubicación de un movimiento es opcional. En dominio se representa mediante `Ubicacion`; en SQLite puede persistirse como latitud/longitud nullable dentro de `movimientos`. osmdroid representa espacialmente esas coordenadas, no es una capa de persistencia.

## Principio de simplicidad

No introducir patrones o capas únicamente para demostrar conocimiento. Cada abstracción debe resolver un problema observable del proyecto.