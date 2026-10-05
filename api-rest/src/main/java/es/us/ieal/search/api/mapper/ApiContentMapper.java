package es.us.ieal.search.api.mapper;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.entity.ContentPage;
import es.us.ieal.search.domain.entity.SearchPage;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import es.us.ieal.search.generated.model.AppliedFilters;
import es.us.ieal.search.generated.model.ContentPageResponse;
import es.us.ieal.search.generated.model.ContentResponse;
import es.us.ieal.search.generated.model.CreateContentRequest;
import es.us.ieal.search.generated.model.SearchItemResponse;
import es.us.ieal.search.generated.model.SearchResponse;
import es.us.ieal.search.generated.model.UpdateContentRequest;

import java.net.URI;
import java.util.List;

public final class ApiContentMapper {
    private ApiContentMapper() {
    }

    public static ContentItem toDomain(CreateContentRequest request) {
        return toDomain(request.getSourceId(), request.getSourceSystem().getValue(),
                request.getSourceUrl(), request.getSlug(), request.getTitle(),
                request.getSummary(), request.getBody(), request.getContentType().getValue(),
                request.getSection(), request.getCategories(), request.getTags(),
                request.getKeywords(), request.getLanguage(), request.getPublishedAt(),
                request.getLastUpdatedAt(), request.getEventStartAt(), request.getEventEndAt(),
                request.getLocation(), request.getOrganizer(), request.getStatus().getValue(),
                request.getVisible());
    }

    public static ContentItem toDomain(UpdateContentRequest request) {
        return toDomain(request.getSourceId(), request.getSourceSystem().getValue(),
                request.getSourceUrl(), request.getSlug(), request.getTitle(),
                request.getSummary(), request.getBody(), request.getContentType().getValue(),
                request.getSection(), request.getCategories(), request.getTags(),
                request.getKeywords(), request.getLanguage(), request.getPublishedAt(),
                request.getLastUpdatedAt(), request.getEventStartAt(), request.getEventEndAt(),
                request.getLocation(), request.getOrganizer(), request.getStatus().getValue(),
                request.getVisible());
    }

    private static ContentItem toDomain(String sourceId, String sourceSystem, URI sourceUrl,
                                        String slug, String title, String summary, String body,
                                        String contentType, String section, List<String> categories,
                                        List<String> tags, List<String> keywords, String language,
                                        java.time.OffsetDateTime publishedAt,
                                        java.time.OffsetDateTime lastUpdatedAt,
                                        java.time.OffsetDateTime eventStartAt,
                                        java.time.OffsetDateTime eventEndAt,
                                        String location, String organizer, String status,
                                        Boolean visible) {
        return new ContentItem(null, sourceId, SourceSystem.valueOf(sourceSystem),
                sourceUrl == null ? null : sourceUrl.toString(), slug, title, summary, body,
                ContentType.valueOf(contentType), section, safe(categories), safe(tags),
                safe(keywords), language, publishedAt, lastUpdatedAt, null, eventStartAt,
                eventEndAt, location, organizer, ContentStatus.valueOf(status),
                Boolean.TRUE.equals(visible), "");
    }

    public static ContentResponse toResponse(ContentItem item) {
        ContentResponse response = new ContentResponse();
        response.setId(item.id());
        response.setSourceId(item.sourceId());
        response.setSourceSystem(es.us.ieal.search.generated.model.SourceSystem.fromValue(
                item.sourceSystem().name()));
        response.setSourceUrl(uri(item.sourceUrl()));
        response.setSlug(item.slug());
        response.setTitle(item.title());
        response.setSummary(item.summary());
        response.setBody(item.body());
        response.setContentType(es.us.ieal.search.generated.model.ContentType.fromValue(
                item.contentType().name()));
        response.setSection(item.section());
        response.setCategories(item.categories());
        response.setTags(item.tags());
        response.setKeywords(item.keywords());
        response.setLanguage(item.language());
        response.setPublishedAt(item.publishedAt());
        response.setLastUpdatedAt(item.lastUpdatedAt());
        response.setIndexedAt(item.indexedAt());
        response.setEventStartAt(item.eventStartAt());
        response.setEventEndAt(item.eventEndAt());
        response.setLocation(item.location());
        response.setOrganizer(item.organizer());
        response.setStatus(es.us.ieal.search.generated.model.ContentStatus.fromValue(
                item.status().name()));
        response.setVisible(item.visible());
        return response;
    }

    public static ContentPageResponse toPageResponse(ContentPage page) {
        ContentPageResponse response = new ContentPageResponse();
        response.setTotal(page.total());
        response.setPage(page.page());
        response.setSize(page.size());
        response.setItems(page.items().stream().map(ApiContentMapper::toResponse).toList());
        return response;
    }

    public static SearchResponse toSearchResponse(SearchPage page, String query,
                                                   ContentType type, String section,
                                                   String category, String sort) {
        SearchResponse response = new SearchResponse();
        response.setTotal(page.total());
        response.setPage(page.page());
        response.setSize(page.size());
        response.setResults(page.results().stream().map(scored -> {
            ContentItem item = scored.content();
            SearchItemResponse result = new SearchItemResponse();
            result.setId(item.id());
            result.setTitle(item.title());
            result.setSummary(item.summary());
            result.setContentType(es.us.ieal.search.generated.model.ContentType.fromValue(
                    item.contentType().name()));
            result.setSection(item.section());
            result.setCategories(item.categories());
            result.setSourceUrl(uri(item.sourceUrl()));
            result.setPublishedAt(item.publishedAt());
            result.setEventStartAt(item.eventStartAt());
            result.setEventEndAt(item.eventEndAt());
            result.setLocation(item.location());
            result.setOrganizer(item.organizer());
            result.setScore(scored.score());
            return result;
        }).toList());

        AppliedFilters filters = new AppliedFilters();
        filters.setQuery(query);
        if (type != null) {
            filters.setType(es.us.ieal.search.generated.model.ContentType.fromValue(type.name()));
        }
        filters.setSection(section);
        filters.setCategory(category);
        filters.setSort(sort);
        response.setFilters(filters);
        return response;
    }

    private static URI uri(String value) {
        return value == null || value.isBlank() ? null : URI.create(value);
    }

    private static <T> List<T> safe(List<T> values) {
        return values == null ? List.of() : values;
    }
}
