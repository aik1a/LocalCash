Overpass principal HTTP 502 e instancia kumi HTTP 500. Conforme al fallback permitido por ADR-002 se intentará un único extracto vectorial de la API oficial de OSM, misma bbox con padding, sin servidores de mosaicos.

Resultado: la petición con padding y la bbox completa fueron rechazadas por >50000 nodos. Se obtuvieron cuatro cuadrantes de la bbox exacta una sola vez; se conservaron 3279 ways/15023 nodos con geometrías completas y hashes/fechas/URLs en download-provenance.json. El atlas versionado es reproducible sin consultas OSM en CI/Android.
