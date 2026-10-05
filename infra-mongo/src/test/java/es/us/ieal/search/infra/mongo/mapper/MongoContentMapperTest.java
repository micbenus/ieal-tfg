package es.us.ieal.search.infra.mongo.mapper;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoContentMapperTest {
    @Test
    void conservaLosCamposPrincipalesEnLaConversion() {
        ContentItem item = new ContentItem("1", "source-1", SourceSystem.LOCAL_JSON,
                null, "el-instituto", "El Instituto", "Resumen", "Cuerpo",
                ContentType.PAGE, "Instituto", List.of("Institucional"), List.of(),
                List.of("IEAL"), "es", OffsetDateTime.now(), OffsetDateTime.now(),
                OffsetDateTime.now(), null, null, null, null,
                ContentStatus.INDEXED, true, "el instituto");

        ContentItem mapped = MongoContentMapper.toDomain(MongoContentMapper.toDocument(item));

        assertEquals(item.id(), mapped.id());
        assertEquals(item.title(), mapped.title());
        assertEquals(item.contentType(), mapped.contentType());
        assertEquals(item.publishedAt().toInstant(), mapped.publishedAt().toInstant());
        assertEquals(item.indexedAt().toInstant(), mapped.indexedAt().toInstant());
        assertEquals(item.searchTextNormalized(), mapped.searchTextNormalized());
    }
}
