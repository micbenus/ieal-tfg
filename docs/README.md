# Documentación del proyecto

Este directorio reúne la documentación técnica que acompaña al código del
prototipo. La memoria académica y las capturas originales se conservan fuera
del repositorio público para evitar duplicados y datos personales innecesarios.

## Documentos principales

- [Descripción técnica](TFG_BUSCADOR_IEAL.md): alcance, arquitectura, flujo de
  búsqueda, ejecución y limitaciones.
- [Requisitos y pruebas](REQUISITOS_Y_PRUEBAS_TFG.md): requisitos funcionales y
  no funcionales, criterios de aceptación y trazabilidad.
- [Diagramas](diagrams/README.md): fuentes PlantUML y versiones SVG.

## Fuente de verdad

- El comportamiento de la API se define en
  `api-rest/src/main/resources/openapi/openapi.yaml`.
- Los datos iniciales de la demostración están en
  `boot/src/main/resources/data/seed-contents.json`.
- Los resultados de compilación y el código generado se crean bajo `target/` y
  no se versionan.

Las cifras de pruebas incluidas en los documentos corresponden a ejecuciones
locales fechadas. Deben repetirse si se modifica el código antes de presentar
nuevas evidencias.
