package es.us.ieal.search.domain.entity;

import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;

import java.time.OffsetDateTime;
import java.util.List;

public record ContentItem(
        String id,
        String sourceId,
        SourceSystem sourceSystem,
        String sourceUrl,
        String slug,
        String title,
        String summary,
        String body,
        ContentType contentType,
        String section,
        List<String> categories,
        List<String> tags,
        List<String> keywords,
        String language,
        OffsetDateTime publishedAt,
        OffsetDateTime lastUpdatedAt,
        OffsetDateTime indexedAt,
        OffsetDateTime eventStartAt,
        OffsetDateTime eventEndAt,
        String location,
        String organizer,
        ContentStatus status,
        boolean visible,
        String searchTextNormalized
) {
    public ContentItem {
        requireText(sourceId, "sourceId");
        if (sourceSystem == null) throw new IllegalArgumentException("sourceSystem es obligatorio");
        requireText(slug, "slug");
        requireText(title, "title");
        if (contentType == null) throw new IllegalArgumentException("contentType es obligatorio");
        requireText(language, "language");
        if (status == null) throw new IllegalArgumentException("status es obligatorio");
        categories = safeList(categories);
        tags = safeList(tags);
        keywords = safeList(keywords);
        summary = safeText(summary);
        body = safeText(body);
        section = safeText(section);
        location = safeText(location);
        organizer = safeText(organizer);
        searchTextNormalized = safeText(searchTextNormalized);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " es obligatorio");
        }
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }
}
