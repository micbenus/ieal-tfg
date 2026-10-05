# IEAL TFG

Prototipo desarrollado como Trabajo Fin de Grado para registrar, indexar y
buscar contenidos del Instituto Universitario de Estudios sobre América Latina
(IEAL) de la Universidad de Sevilla. El repositorio contiene una aplicación
REST ejecutable en local, sus pruebas automatizadas, datos de demostración y la
documentación técnica necesaria para comprender las decisiones adoptadas.

El objetivo es validar el flujo de búsqueda y una arquitectura compatible con
una futura integración con WordPress. No reproduce el portal institucional ni
se presenta como una solución preparada para producción.

## Alcance del MVP

### Implementado

- Alta individual y en lote de contenidos.
- Tipos `PAGE`, `NEWS`, `EVENT`, `CALL` y `PUBLICATION`.
- Consulta de detalle, actualización y reindexado.
- Listado administrativo paginado con filtro opcional por tipo.
- Búsqueda sobre título, resumen, cuerpo, palabras clave y categorías.
- Normalización de minúsculas, espacios y tildes.
- Filtros por tipo, sección y categoría.
- Paginación y orden por relevancia o fecha.
- Persistencia MongoDB Community.
- Doce contenidos de demostración cargados de forma idempotente únicamente con el perfil `dev`.
- Metadatos opcionales de evento: inicio, fin, lugar y organizador.
- Contrato OpenAPI que genera interfaces y DTOs durante la compilación.
- Swagger UI, gestión uniforme de errores y pruebas esenciales.

### Puntos de extensión

- `ExternalContentProviderPort` define el contrato previsto para proveedores externos.
- `WordpressContentAdapter` delimita el lugar de una posible integración con WordPress REST API; no está activo ni registrado como componente.
- El dominio representa contenido con independencia de su origen.
- Es posible sustituir el algoritmo local por búsquedas Mongo más avanzadas o
  un motor especializado sin cambiar los controladores.

### Fuera de alcance

- Conexión real con WordPress, idUS, Prisma u otros sistemas universitarios.
- Autenticación, autorización y despliegue productivo.
- Sincronización automática, colas, frontend y búsqueda semántica.
- Elasticsearch, alta disponibilidad y optimización para grandes volúmenes.

## Arquitectura

El proyecto usa un único servicio Maven multi-módulo:

```text
api-rest -> application -> domain
                     ^
                     |
               infra-mongo -> MongoDB
                     |
              futuro WordPress

boot -> composición y arranque de todos los módulos
```

| Módulo | Responsabilidad |
| --- | --- |
| `domain` | Entidad, enums, reglas de normalización/relevancia y puertos |
| `application` | Implementación de casos de uso y coordinación |
| `infra-mongo` | Documento, repositorio y adapter de persistencia |
| `api-rest` | OpenAPI, interfaces generadas, controladores y errores |
| `boot` | Arranque, wiring, propiedades y carga de datos |

Flujo principal:

```text
Controller -> ContentUseCase -> ContentService
           -> ContentRepositoryPort -> MongoContentRepositoryAdapter
           -> Spring Data Repository -> MongoDB
```

El código Java derivado de OpenAPI no se versiona. Maven lo genera en
`api-rest/target/generated-sources/openapi` durante la compilación.

La documentación ampliada se encuentra en [docs/README.md](docs/README.md).

## Tecnologías

- Java 17
- Spring Boot 3.3
- Maven multi-módulo
- MongoDB Community 7
- Spring Data MongoDB
- OpenAPI Generator y Springdoc
- JUnit 5, Mockito y MockMvc

## Requisitos

- JDK 17
- Maven 3.9 o superior
- Docker con Docker Compose, o MongoDB Community instalado localmente

## Ejecución local

1. Levantar Mongo:

```bash
docker compose up -d mongodb
```

2. Generar OpenAPI, compilar y ejecutar pruebas:

```bash
mvn clean verify
```

3. Arrancar la aplicación con los datos de demostración:

```bash
SPRING_PROFILES_ACTIVE=dev java -jar boot/target/boot-0.1.0-SNAPSHOT.jar
```

También puede arrancarse después de `mvn install`:

```bash
mvn -f boot/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev
```

La conexión puede cambiarse sin modificar código:

```bash
MONGODB_URI=mongodb://localhost:27017/ieal_search java -jar boot/target/boot-0.1.0-SNAPSHOT.jar
```

## OpenAPI y Swagger

La fuente de verdad es:

```text
api-rest/src/main/resources/openapi/openapi.yaml
```

Generar únicamente interfaces y DTOs:

```bash
mvn generate-sources
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Contrato detectado por Springdoc:

```text
http://localhost:8080/v3/api-docs
```

## Uso de la API

Buscar por palabra clave:

```bash
curl "http://localhost:8080/api/v1/search?q=investigacion"
```

Listar contenidos registrados:

```bash
curl "http://localhost:8080/api/v1/contents?type=EVENT&page=0&size=20"
```

Filtrar eventos y ordenar por fecha:

```bash
curl "http://localhost:8080/api/v1/search?q=america&type=EVENT&sort=date"
```

Filtrar por categoría:

```bash
curl "http://localhost:8080/api/v1/search?q=publicacion&category=Investigacion"
```

Registrar contenido:

```bash
curl -X POST "http://localhost:8080/api/v1/contents" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceId": "manual-001",
    "sourceSystem": "MANUAL",
    "slug": "seminario-ejemplo",
    "title": "Seminario sobre América Latina",
    "summary": "Contenido de demostración",
    "body": "Descripción del seminario.",
    "contentType": "EVENT",
    "section": "Actividades",
    "categories": ["Seminarios"],
    "tags": ["América Latina"],
    "keywords": ["seminario"],
    "language": "es",
    "status": "INDEXED",
    "visible": true
  }'
```

La respuesta de búsqueda contiene `total`, `page`, `size`, resultados resumidos,
el `score` de cada resultado y los filtros aplicados. El cuerpo completo se
obtiene mediante `GET /api/v1/contents/{id}`. Los errores usan siempre
`timestamp`, `status`, `error`, `message` y `path`.

## Modelo de contenido

`ContentItem` almacena identificadores internos y de origen, URL y slug,
texto principal, tipo, clasificación, idioma, fechas, estado, visibilidad y
`searchTextNormalized`. Para eventos también admite inicio, fin, lugar y
organizador. `sourceSystem` distingue altas manuales, datos JSON y el futuro
origen WordPress.

`searchTextNormalized` es un detalle interno de indexación: se guarda en Mongo,
pero no forma parte de las respuestas públicas. Los contratos de alta,
actualización, detalle y resultado de búsqueda están separados para no exponer
campos innecesarios.

La relevancia del MVP asigna más peso al título, peso medio a resumen,
palabras clave y categorías, y menor peso al cuerpo. El cálculo se realiza en
la aplicación sobre los contenidos visibles recuperados de Mongo. Es sencillo
de explicar y suficiente para los datos de demostración, pero no está pensado
para grandes volúmenes.

## Decisiones de diseño

- **Hexagonal/DDD:** separa reglas, casos de uso y tecnología externa. Aquí
  aporta una frontera clara para la futura integración con WordPress.
- **MongoDB:** encaja con contenidos heterogéneos y listas variables de
  categorías, etiquetas y palabras clave, y puede ejecutarse gratis en local.
- **OpenAPI primero:** evita divergencias entre documentación, DTOs e
  interfaces REST. Los controladores implementan el código generado.
- **Dominio sin WordPress:** el buscador trabaja igual con datos manuales,
  JSON, Mongo o un futuro proveedor REST.
- **MVP limitado:** se demuestra el flujo de contenido indexable y búsqueda
  sin introducir seguridad, mensajería ni infraestructura innecesaria.
- **Duplicados:** se identifican por la combinación `sourceSystem + sourceId`.
- **Categoría:** el MVP acepta un único filtro `category`, aunque cada contenido
  pueda tener varias categorías.

## Pruebas

```bash
mvn test
```

La cobertura esencial incluye normalización y relevancia, alta, búsqueda,
listado y filtro por tipo, contenido inexistente, mapeo Mongo, mapeo API y
respuesta web sin resultados. Mongo se mantiene fuera de las pruebas automáticas para evitar
que la defensa dependa de Testcontainers; la persistencia se comprueba en la
demo local con Docker Compose.

## Iteraciones

1. **Iteración 1:** modelo, persistencia Mongo, seeds y búsqueda local.
2. **Iteración 2:** filtros, paginación, detalle, alta y actualización.
3. **Iteración 3:** reindexado, puntuación de relevancia, OpenAPI y pruebas.
4. **Futura:** implementar `WordpressContentAdapter` con WordPress REST API.
5. **Futura:** sincronización automática e incremental desde WordPress.
6. **Futura:** conectores con idUS/Prisma y control de permisos.
7. **Futura:** frontend de búsqueda integrado en el portal.

## Limitaciones

- La búsqueda carga los contenidos visibles y calcula la relevancia en memoria.
- El alta en lote se procesa secuencialmente y no es transaccional.
- `PUT` reemplaza los metadatos editables completos.
- El adapter WordPress es únicamente un punto de extensión, no una integración.
- No se incluyen métricas de negocio, seguridad ni procedimientos de despliegue.

Estas limitaciones son conscientes: mantienen el prototipo pequeño, ejecutable
y defendible, y dejan las mejoras de producción como trabajo futuro explícito.

## Documentación para la memoria

- [Documentación técnica del prototipo](docs/TFG_BUSCADOR_IEAL.md)
- [Requisitos, criterios de aceptación y pruebas Swagger](docs/REQUISITOS_Y_PRUEBAS_TFG.md)
- [Diagrama de arquitectura](docs/diagrams/arquitectura.puml)
- [Diagrama de secuencia de búsqueda](docs/diagrams/secuencia-busqueda.puml)
