# Documentación técnica del prototipo Buscador IEAL

## 1. Descripción y objetivo

Este proyecto es un servicio REST independiente creado para experimentar con una búsqueda transversal de contenidos relacionados con el Instituto Universitario de Estudios sobre América Latina (IEAL) de la Universidad de Sevilla. Permite localizar información sin conocer de antemano si corresponde a una página institucional, noticia, actividad o publicación.

El prototipo no sustituye WordPress, no está conectado al portal en producción y no sincroniza automáticamente información institucional. Los datos incluidos son copias de demostración basadas en fuentes públicas y sus URL se conservan para trazabilidad.

## 2. Tecnologías

| Tecnología | Función en el proyecto |
| --- | --- |
| Java 17 | Lenguaje y versión de ejecución. |
| Spring Boot 3.3.5 | Arranque, API web, configuración y composición. |
| Maven | Construcción de los cinco módulos y ejecución de pruebas. |
| MongoDB Community 7 | Persistencia documental en la colección `contents`. |
| Docker Compose | Arranque reproducible de MongoDB en local. |
| OpenAPI 3.0.3 | Contrato de endpoints, parámetros y respuestas. |
| Swagger UI / Springdoc | Exploración y ejecución manual de la API. |
| JUnit 5, Mockito y MockMvc | Pruebas unitarias y web aisladas. |

## 3. Arquitectura

Se utiliza una arquitectura hexagonal simplificada dentro de una única aplicación Maven multimódulo. No son microservicios.

| Módulo | Responsabilidad | Dependencias relevantes |
| --- | --- | --- |
| `domain` | Entidades, value objects, reglas y puertos. | No depende de Spring ni MongoDB. |
| `application` | Implementa los casos de uso en `ContentService`. | Depende de `domain`. |
| `infra-mongo` | Implementa persistencia y mapeo MongoDB. | Depende de `domain`. |
| `api-rest` | Controladores, errores, OpenAPI y DTO generados. | Depende de `application`. |
| `boot` | Arranque, wiring y seed de desarrollo. | Integra `api-rest` e `infra-mongo`. |

Los diagramas fuente están en [arquitectura.puml](diagrams/arquitectura.puml) y [secuencia-busqueda.puml](diagrams/secuencia-busqueda.puml).

## 4. Clases principales

| Clase | Módulo | Responsabilidad |
| --- | --- | --- |
| `ContentItem` | domain | Modelo de contenido independiente de MongoDB y HTTP. |
| `SearchQuery` | domain | Valida que la consulta sea obligatoria. |
| `SearchFilters` | domain | Agrupa filtros, paginación y orden. |
| `TextNormalizer` | domain | Convierte a minúsculas, elimina tildes y compacta espacios. |
| `RelevanceScorer` | domain | Calcula relevancia con pesos simples. |
| `ContentUseCase` | domain | Puerto de entrada de los casos de uso. |
| `ContentRepositoryPort` | domain | Puerto de salida para persistencia. |
| `ContentService` | application | Coordina alta, consulta, búsqueda, filtros, orden y paginación. |
| `MongoContentRepositoryAdapter` | infra-mongo | Implementa el puerto usando Spring Data MongoDB. |
| `ContentDocument` | infra-mongo | Representa cada documento de la colección `contents`. |
| `SearchController` | api-rest | Recibe `GET /api/v1/search`. |
| `ContentsController` | api-rest | Expone listado, detalle, alta, actualización y reindexado. |
| `HealthController` | api-rest | Expone el estado básico del servicio. |
| `ApiExceptionHandler` | api-rest | Transforma errores controlados en respuestas HTTP uniformes. |
| `SeedDataLoader` | boot | Carga los documentos demo únicamente en perfil `dev`. |
| `SearchApplication` | boot | Punto de entrada Spring Boot. |

## 5. Flujo real de una búsqueda

Para `GET /api/v1/search?q=america&page=0&size=20`:

1. `SearchController` recibe los parámetros definidos por OpenAPI.
2. Construye `SearchQuery` y `SearchFilters`; estos validan consulta y paginación.
3. Invoca `ContentService.search` mediante `ContentUseCase`.
4. El servicio solicita los contenidos visibles a `ContentRepositoryPort`.
5. `MongoContentRepositoryAdapter` usa `SpringDataContentRepository` para leer MongoDB.
6. `MongoContentMapper` convierte `ContentDocument` a `ContentItem`.
7. El servicio aplica los filtros opcionales.
8. `RelevanceScorer` normaliza la consulta y puntúa cada contenido.
9. Se eliminan resultados con puntuación cero, se ordenan y se pagina la lista.
10. `ApiContentMapper` construye el DTO y Spring devuelve JSON.

La relevancia asigna 5 puntos a coincidencias en título, 3 en resumen, 3 en palabras clave, 2 en categorías y 1 en cuerpo. Los puntos se acumulan. Es una regla explicable para una demo pequeña, no un motor de búsqueda productivo.

## 6. Datos de demostración

El fichero `boot/src/main/resources/data/seed-contents.json` contiene 12 documentos. Representan páginas institucionales, personas, noticias, cooperación, eventos y publicaciones. Los campos descriptivos están resumidos para la demostración y cada documento mantiene una URL pública de referencia.

Con `dev`, `SeedDataLoader` lee el JSON y crea los documentos mediante el mismo caso de uso utilizado por la API. La combinación `sourceSystem + sourceId` evita duplicados al reiniciar. Sin el perfil `dev` no se realiza carga automática.

Ejemplo simplificado:

```json
{
  "title": "Miguel Ángel Martín López, director del IEAL",
  "contentType": "NEWS",
  "tags": ["Miguel Ángel Martín López", "IEAL"],
  "sourceSystem": "LOCAL_JSON"
}
```

Al buscar `miguel`, el título aporta la mayor parte de la puntuación y el elemento se devuelve con su `sourceUrl`.

## 7. Endpoints

| Método | Ruta | Función | Respuestas principales |
| --- | --- | --- | --- |
| GET | `/api/v1/health` | Estado básico. | 200 |
| GET | `/api/v1/search` | Búsqueda, filtros, orden y paginación. | 200, 400, 500 |
| GET | `/api/v1/contents` | Listado paginado. | 200, 400, 500 |
| POST | `/api/v1/contents` | Alta individual. | 201, 400, 409 |
| POST | `/api/v1/contents/bulk` | Alta de un lote. | 201, 400, 409 |
| GET | `/api/v1/contents/{id}` | Detalle. | 200, 404 |
| PUT | `/api/v1/contents/{id}` | Actualización completa. | 200, 400, 404 |
| POST | `/api/v1/contents/{id}/reindex` | Regenera texto normalizado. | 200, 404 |

El contrato completo y los esquemas están en `api-rest/src/main/resources/openapi/openapi.yaml`.

## 8. Ejecución reproducible

Requisitos: JDK 17, Maven 3.9 o superior y Docker.

```bash
docker compose up -d mongodb
mvn clean package
SPRING_PROFILES_ACTIVE=dev java -jar boot/target/boot-0.1.0-SNAPSHOT.jar
```

Swagger UI: `http://localhost:8080/swagger-ui.html`.

Comprobación rápida:

```bash
curl http://localhost:8080/api/v1/health
curl "http://localhost:8080/api/v1/search?q=america"
curl "http://localhost:8080/api/v1/search?q=miguel"
curl "http://localhost:8080/api/v1/contents?page=0&size=20"
```

## 9. Estrategia de pruebas

Las pruebas automáticas verifican reglas de relevancia, normalización asociada, casos de uso, filtros, paginación, mapeos, health, respuestas vacías y errores básicos. No levantan MongoDB: la persistencia y el flujo completo se verifican en la demostración local.

Los requisitos, criterios de aceptación, matriz de trazabilidad y guion de capturas Swagger están en [REQUISITOS_Y_PRUEBAS_TFG.md](REQUISITOS_Y_PRUEBAS_TFG.md). Los resultados numéricos de Maven y de las llamadas reales deben anotarse después de cada ejecución; no deben copiarse resultados antiguos como si fueran una nueva evidencia.

### Ejecución verificada el 20/09/2026

| Prueba ejecutada | Resultado real |
| --- | --- |
| `mvn package` | BUILD SUCCESS |
| Tests automáticos | 13 ejecutados, 0 fallos, 0 errores, 0 omitidos |
| Arranque MongoDB 7 con Compose | Contenedor iniciado correctamente |
| Arranque del JAR con perfil `dev` | Aplicación iniciada en el puerto 8080 |
| Conteo directo `ieal_search.contents` | 12 documentos |
| Segundo arranque con perfil `dev` | 12 documentos; carga idempotente |
| `GET /api/v1/health` | HTTP 200, `status=UP` |
| Búsqueda `america` | HTTP 200, 8 resultados |
| Búsqueda `miguel` | HTTP 200, 4 resultados |
| Búsqueda inexistente | HTTP 200, 0 resultados |
| Filtro `alimentacion` + `PUBLICATION` | HTTP 200, 1 resultado del tipo solicitado |
| Paginación `america&page=0&size=1` | HTTP 200, total 8 y 1 elemento devuelto |
| Consulta sin `q` | HTTP 400 con JSON de error |
| Paginación `size=0` | HTTP 400 con JSON de error |
| `/v3/api-docs` | HTTP 200 |
| `/swagger-ui.html` siguiendo redirección | HTTP 200 |

Durante la primera ejecución integral se detectaron dos errores que no aparecían en las pruebas aisladas: MongoDB no serializaba `OffsetDateTime` y determinadas validaciones web acababan en HTTP 500. Se corrigió la persistencia almacenando los instantes como `Instant` y se ampliaron las excepciones consideradas petición inválida. Las pruebas se repitieron después de las correcciones.

## 10. Limitaciones y trabajo futuro

- No existe integración con WordPress, idUS o PRISMA.
- No hay autenticación, autorización ni despliegue de producción.
- Los datos son una muestra local y no se sincronizan con sus fuentes.
- La búsqueda carga los documentos visibles y calcula relevancia en memoria.
- No se han realizado pruebas de carga, seguridad ni alta disponibilidad.
- `WordpressContentAdapter` es un punto de extensión, no una integración activa.

Una integración futura podría hacer que un formulario WordPress invoque la API por HTTP y renderice el JSON recibido. Esa integración queda expresamente fuera del prototipo validado.
