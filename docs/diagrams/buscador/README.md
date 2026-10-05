# Diagramas del buscador IEAL

Cada diagrama se conserva en dos formatos:

- `.puml`: código fuente PlantUML, editable y regenerable.
- `.svg`: imagen vectorial para abrir en navegador o insertar en Word, LibreOffice o PowerPoint sin perder nitidez.

## Archivos

- `01-arquitectura-buscador-ieal`: arquitectura hexagonal del prototipo.
- `02-clases-buscador-ieal`: clases y puertos principales.
- `03-secuencia-busqueda-wordpress`: consumo futuro desde WordPress.
- `04-estructura-carpetas-buscador`: organización Maven por módulos.
- `05-arquitectura-hexagonal-simplificada-buscador-ieal`: vista resumida de puertos y adaptadores.

## Regenerar imágenes desde los fuentes

Desde la raiz del proyecto:

```bash
plantuml -tsvg docs/diagrams/buscador/*.puml
```

Para generar PNG en lugar de SVG:

```bash
plantuml -tpng docs/diagrams/buscador/*.puml
```
