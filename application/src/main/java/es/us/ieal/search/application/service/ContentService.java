package es.us.ieal.search.application.service;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.entity.ContentPage;
import es.us.ieal.search.domain.entity.ScoredContent;
import es.us.ieal.search.domain.entity.SearchPage;
import es.us.ieal.search.domain.exception.ContentNotFoundException;
import es.us.ieal.search.domain.exception.DuplicateContentException;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import es.us.ieal.search.domain.port.out.ContentRepositoryPort;
import es.us.ieal.search.domain.service.RelevanceScorer;
import es.us.ieal.search.domain.service.TextNormalizer;
import es.us.ieal.search.domain.valueobject.SearchFilters;
import es.us.ieal.search.domain.valueobject.SearchQuery;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContentService implements ContentUseCase {
    private final ContentRepositoryPort repository;

    public ContentService(ContentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public ContentItem create(ContentItem content) {
        if (repository.existsBySourceSystemAndSourceId(
                content.sourceSystem().name(), content.sourceId())) {
            throw new DuplicateContentException(content.sourceId());
        }
        return repository.save(indexedCopy(content, content.id()));
    }

    @Override
    public List<ContentItem> createBulk(List<ContentItem> contents) {
        List<ContentItem> created = new ArrayList<>();
        for (ContentItem content : contents) {
            created.add(create(content));
        }
        return created;
    }

    @Override
    public ContentItem getById(String id) {
        return repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
    }

    @Override
    public ContentItem update(String id, ContentItem content) {
        getById(id);
        return repository.save(indexedCopy(content, id));
    }

    @Override
    public ContentItem reindex(String id) {
        return repository.save(indexedCopy(getById(id), id));
    }

    @Override
    public ContentPage list(ContentType contentType, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Paginación inválida");
        }
        List<ContentItem> contents = repository.findAll().stream()
                .filter(item -> contentType == null || item.contentType() == contentType)
                .sorted(Comparator.comparing(ContentItem::lastUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        int from = Math.min(page * size, contents.size());
        int to = Math.min(from + size, contents.size());
        return new ContentPage(contents.size(), page, size, contents.subList(from, to));
    }

    @Override
    public SearchPage search(SearchQuery query, SearchFilters filters) {
        List<ScoredContent> matches = repository.findAllVisible().stream()
                .filter(item -> filters.contentType() == null
                        || item.contentType() == filters.contentType())
                .filter(item -> isBlank(filters.section())
                        || normalizedEquals(item.section(), filters.section()))
                .filter(item -> isBlank(filters.category())
                        || item.categories().stream()
                        .anyMatch(category -> normalizedEquals(category, filters.category())))
                .map(item -> new ScoredContent(item, RelevanceScorer.score(item, query.value())))
                .filter(result -> result.score() > 0)
                .sorted(comparator(filters.sortOrder()))
                .toList();

        int from = Math.min(filters.page() * filters.size(), matches.size());
        int to = Math.min(from + filters.size(), matches.size());
        return new SearchPage(matches.size(), filters.page(), filters.size(), matches.subList(from, to));
    }

    private ContentItem indexedCopy(ContentItem item, String id) {
        OffsetDateTime now = OffsetDateTime.now();
        ContentItem base = new ContentItem(id, item.sourceId(), item.sourceSystem(), item.sourceUrl(),
                item.slug(), item.title(), item.summary(), item.body(), item.contentType(),
                item.section(), item.categories(), item.tags(), item.keywords(), item.language(),
                item.publishedAt(), item.lastUpdatedAt() == null ? now : item.lastUpdatedAt(),
                now, item.eventStartAt(), item.eventEndAt(), item.location(), item.organizer(),
                item.status(), item.visible(), "");
        return new ContentItem(base.id(), base.sourceId(), base.sourceSystem(), base.sourceUrl(),
                base.slug(), base.title(), base.summary(), base.body(), base.contentType(),
                base.section(), base.categories(), base.tags(), base.keywords(), base.language(),
                base.publishedAt(), base.lastUpdatedAt(), base.indexedAt(), base.eventStartAt(),
                base.eventEndAt(), base.location(), base.organizer(), base.status(), base.visible(),
                TextNormalizer.searchableText(base));
    }

    private Comparator<ScoredContent> comparator(SearchFilters.SortOrder order) {
        if (order == SearchFilters.SortOrder.DATE) {
            return Comparator.comparing(
                    result -> result.content().publishedAt(),
                    Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return Comparator.comparingDouble(ScoredContent::score).reversed()
                .thenComparing(result -> result.content().publishedAt(),
                        Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private boolean normalizedEquals(String left, String right) {
        return TextNormalizer.normalize(left).equals(TextNormalizer.normalize(right));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
