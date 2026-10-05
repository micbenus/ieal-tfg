# Requisitos y plan de pruebas del Buscador IEAL

## 1. Alcance

El sistema es un prototipo REST independiente para almacenar y buscar contenidos de demostración relacionados con el IEAL. No sustituye al portal WordPress, no está desplegado en producción y no realiza sincronización automática con fuentes institucionales.

## 2. Actores

| Actor | Descripción |
| --- | --- |
| Usuario de consulta | Busca contenidos mediante Swagger UI o un cliente HTTP. |
| Responsable de la demostración | Arranca MongoDB, activa el perfil `dev` y ejecuta las pruebas. |
| WordPress | Consumidor futuro; no está integrado en el prototipo actual. |

## 3. Requisitos funcionales

| ID | Requisito | Prioridad | Evidencia prevista |
| --- | --- | --- | --- |
| RF-01 | El sistema debe informar de su disponibilidad mediante `GET /api/v1/health`. | Alta | PB-01 |
| RF-02 | Debe buscar contenido por una consulta textual obligatoria. | Alta | PB-02 |
| RF-03 | Debe normalizar mayúsculas, tildes y espacios para comparar textos. | Alta | Test de dominio y PB-03 |
| RF-04 | Debe ordenar por una relevancia sencilla y comprensible. | Alta | Test de relevancia y PB-02 |
| RF-05 | Debe filtrar por tipo, sección y categoría. | Media | PB-05 |
| RF-06 | Debe paginar los resultados mediante `page` y `size`. | Media | PB-06 |
| RF-07 | Debe devolver una lista vacía cuando no existen coincidencias. | Alta | PB-04 |
| RF-08 | Debe rechazar consultas o paginaciones inválidas con HTTP 400. | Alta | PB-07 y PB-08 |
| RF-09 | Debe permitir consultar, crear, actualizar, listar y reindexar contenidos. | Media | OpenAPI y PB-09 |
| RF-10 | En perfil `dev`, debe cargar 12 documentos de demostración sin duplicarlos al reiniciar. | Alta | PB-10 |
| RF-11 | Debe persistir los contenidos en la colección `contents` de MongoDB. | Alta | PB-10 |
| RF-12 | Debe publicar el contrato y la interfaz Swagger UI. | Alta | PB-11 |

## 4. Requisitos no funcionales

| ID | Requisito | Criterio verificable |
| --- | --- | --- |
| RNF-01 | Compatibilidad | Compila y se ejecuta con Java 17 y Maven. |
| RNF-02 | Portabilidad local | MongoDB puede arrancarse con `docker compose up -d mongodb`. |
| RNF-03 | Mantenibilidad | Se conservan cinco módulos con responsabilidades separadas. |
| RNF-04 | Simplicidad | No utiliza Elasticsearch, Kafka, Redis, Kubernetes ni autenticación compleja. |
| RNF-05 | Interoperabilidad | La API intercambia JSON y se describe con OpenAPI 3.0.3. |
| RNF-06 | Trazabilidad | Cada dato demo conserva `sourceId`, `sourceSystem` y `sourceUrl`. |
| RNF-07 | Seguridad del entorno | El seed solo se activa con el perfil `dev`. |
| RNF-08 | Fiabilidad básica | Los errores controlados devuelven un cuerpo uniforme y código HTTP adecuado. |
| RNF-09 | Rendimiento del prototipo | Una búsqueda sobre los 12 documentos demo debe responder sin espera apreciable en local. No se establece SLA productivo. |
| RNF-10 | Reproducibilidad | Los comandos y valores de prueba están documentados. |

## 5. Criterios de aceptación

| ID | Dado | Cuando | Entonces |
| --- | --- | --- | --- |
| CA-01 | Servicio iniciado | Se consulta `/api/v1/health` | Responde HTTP 200 y `status=UP`. |
| CA-02 | Seed dev cargado | Se busca `america` | Responde HTTP 200 con al menos un resultado. |
| CA-03 | Seed dev cargado | Se busca `miguel` | Aparece contenido de Miguel Ángel Martín López. |
| CA-04 | Seed dev cargado | Se busca `xyzsinresultado` | Responde HTTP 200, `total=0` y `results=[]`. |
| CA-05 | Servicio iniciado | Se omite `q` o se envía vacío | Responde HTTP 400. |
| CA-06 | Existen varios tipos | Se filtra `type=PUBLICATION` | Todos los resultados son publicaciones. |
| CA-07 | Existen varias coincidencias | Se usa `page=0&size=1` | La respuesta contiene como máximo un resultado y conserva el total. |
| CA-08 | Seed ya cargado | Se reinicia con perfil `dev` | No se duplican los 12 documentos. |
| CA-09 | Swagger disponible | Se abre `/swagger-ui.html` | Se muestran Search, Contents y Health. |

## 6. Pruebas manuales desde Swagger

Preparación: ejecutar MongoDB y la aplicación con perfil `dev`, abrir `http://localhost:8080/swagger-ui.html`, desplegar la operación, pulsar **Try it out**, introducir los parámetros y pulsar **Execute**.

| ID | Operación y valores | Resultado esperado | Captura recomendada |
| --- | --- | --- | --- |
| PB-01 | `GET /api/v1/health` | 200; `status: UP`. | Operación y cuerpo de respuesta. |
| PB-02 | `GET /api/v1/search`, `q=america` | 200; varios resultados con `score`. | Parámetros, URL y primer resultado. |
| PB-03 | `GET /api/v1/search`, `q=miguel` | 200; resultado de Miguel Ángel Martín López. | Título, tipo, URL de origen y score. |
| PB-04 | `GET /api/v1/search`, `q=xyzsinresultado` | 200; `total: 0`, lista vacía. | Código y cuerpo completo. |
| PB-05 | `q=alimentacion`, `type=PUBLICATION` | 200; solo publicaciones. | Parámetros y resultados. |
| PB-06 | `q=america`, `page=0`, `size=1` | 200; un elemento como máximo. | `total`, `page`, `size` y resultado. |
| PB-07 | `GET /api/v1/search` sin `q` | 400; error controlado. | Código 400 y JSON de error. |
| PB-08 | `q=america`, `size=0` | 400; paginación inválida. | Parámetros y error. |
| PB-09 | `GET /api/v1/contents?page=0&size=20` | 200; listado de 12 contenidos si la base estaba vacía. | Total y varios tipos. |
| PB-10 | Reiniciar la aplicación y repetir PB-09 | El total continúa en 12. | Total antes y después. |
| PB-11 | Abrir Swagger UI | Se visualizan los tres grupos del contrato. | Vista general de Swagger. |

Las capturas deben mostrar la fecha de ejecución en el pie de figura o texto de la memoria. No deben presentarse como prueba de producción: son evidencias de una ejecución local controlada.

## 7. Matriz de trazabilidad

| Requisito | Implementación principal | Pruebas |
| --- | --- | --- |
| RF-01 | `HealthController` | `HealthControllerTest`, PB-01 |
| RF-02, RF-07, RF-08 | `SearchController`, `SearchQuery`, `ApiExceptionHandler` | `SearchControllerTest`, PB-02, PB-04, PB-07 |
| RF-03, RF-04 | `TextNormalizer`, `RelevanceScorer` | `RelevanceScorerTest`, PB-02 y PB-03 |
| RF-05, RF-06 | `ContentService`, `SearchFilters` | `ContentServiceTest`, PB-05 y PB-06 |
| RF-09 | `ContentsController`, `ContentService` | OpenAPI, pruebas de servicio, PB-09 |
| RF-10, RF-11 | `SeedDataLoader`, `seed-contents.json`, adapter Mongo | PB-09 y PB-10 |
| RF-12 | `openapi.yaml`, Springdoc | PB-11 |

## 8. Limitaciones de validación

Las pruebas unitarias usan dobles de prueba y no sustituyen la validación manual con MongoDB. Las pruebas Swagger son locales y no demuestran integración con WordPress, seguridad, carga elevada, alta disponibilidad ni despliegue productivo.

## 9. Estado de ejecución

El 20/09/2026 se ejecutaron PB-01 a PB-11 mediante llamadas HTTP equivalentes a las generadas por Swagger. Todas obtuvieron el resultado esperado después de corregir la serialización temporal de MongoDB y el tratamiento de errores de validación. La suite final registró 13 tests, 0 fallos, 0 errores y 0 omitidos. Para la memoria se recomienda repetir manualmente las operaciones desde Swagger y tomar las capturas en el orden de la sección 6, manteniendo visible el código HTTP y el cuerpo JSON.
