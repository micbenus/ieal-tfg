# Descripción del diagrama de clases

El diagrama representa el modelo principal del prototipo de búsqueda del IEAL.
`ContentItem` reúne los metadatos necesarios para indexar y recuperar páginas,
noticias, eventos, convocatorias y publicaciones. `SearchQuery` y
`SearchFilters` encapsulan los criterios de consulta, mientras que
`SearchPage` y `ScoredContent` modelan una respuesta paginada con una
puntuación de relevancia sencilla.

`ContentService` implementa los casos de uso y depende de
`ContentRepositoryPort`. Esta separación permite utilizar MongoDB mediante el
adaptador actual sin introducir dependencias de persistencia en el dominio.
`ExternalContentProviderPort` identifica un posible punto de integración con
proveedores externos; la conexión con WordPress no forma parte del prototipo
ejecutable.
