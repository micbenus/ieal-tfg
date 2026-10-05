# Diagramas PlantUML

Esta carpeta contiene el código fuente de los diagramas del proyecto.

## Organización

- Guardar cada diagrama en un archivo con extensión `.puml`.
- Usar nombres descriptivos en minúsculas, por ejemplo:
  `diagrama-casos-uso.puml`.
- Exportar las imágenes generadas en esta misma carpeta cuando sean
  necesarias para la memoria del TFG.

## Regeneración

Los diagramas requieren PlantUML y una instalación de Java. Graphviz puede ser
necesario según el tipo de diagrama y la distribución utilizada.

```bash
plantuml -tsvg docs/diagrams/*.puml
plantuml -tsvg docs/diagrams/buscador/*.puml
```

Las versiones SVG se conservan junto a los fuentes para poder consultarlas sin
instalar herramientas adicionales. PlantUML también permite generar PNG:

```bash
plantuml -tpng docs/diagrams/buscador/*.puml
```
