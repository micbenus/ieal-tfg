package es.us.ieal.search.infra.mongo.mapper;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import es.us.ieal.search.infra.mongo.entity.ContentDocument;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class MongoContentMapper {
    private MongoContentMapper() {
    }

    public static ContentDocument toDocument(ContentItem item) {
        return new ContentDocument(item.id(), item.sourceId(), item.sourceSystem().name(),
                item.sourceUrl(), item.slug(), item.title(), item.summary(), item.body(),
                item.contentType().name(), item.section(), item.categories(), item.tags(),
                item.keywords(), item.language(), toInstant(item.publishedAt()),
                toInstant(item.lastUpdatedAt()), toInstant(item.indexedAt()),
                toInstant(item.eventStartAt()), toInstant(item.eventEndAt()), item.location(),
                item.organizer(), item.status().name(), item.visible(),
                item.searchTextNormalized());
    }

    public static ContentItem toDomain(ContentDocument document) {
        return new ContentItem(document.getId(), document.getSourceId(),
                SourceSystem.valueOf(document.getSourceSystem()), document.getSourceUrl(),
                document.getSlug(), document.getTitle(), document.getSummary(),
                document.getBody(), ContentType.valueOf(document.getContentType()),
                document.getSection(), document.getCategories(), document.getTags(),
                document.getKeywords(), document.getLanguage(), toOffsetDateTime(document.getPublishedAt()),
                toOffsetDateTime(document.getLastUpdatedAt()), toOffsetDateTime(document.getIndexedAt()),
                toOffsetDateTime(document.getEventStartAt()), toOffsetDateTime(document.getEventEndAt()),
                document.getLocation(),
                document.getOrganizer(),
                ContentStatus.valueOf(document.getStatus()), document.isVisible(),
                document.getSearchTextNormalized());
    }

    private static Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    private static OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
