package es.us.ieal.search.domain.service;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelevanceScorerTest {
    @Test
    void normalizaTildesYPriorizaElTitulo() {
        ContentItem item = content("Líneas de investigación", "Texto general");

        assertEquals("lineas de investigacion", TextNormalizer.normalize(item.title()));
        assertTrue(RelevanceScorer.score(item, "investigacion") >= 5);
    }

    private ContentItem content(String title, String body) {
        return new ContentItem(null, "source-1", SourceSystem.MANUAL, null, "slug", title,
                "", body, ContentType.PAGE, "Instituto", List.of(), List.of(),
                List.of(), "es", OffsetDateTime.now(), OffsetDateTime.now(), null,
                null, null, null, null,
                ContentStatus.INDEXED, true, "");
    }
}
