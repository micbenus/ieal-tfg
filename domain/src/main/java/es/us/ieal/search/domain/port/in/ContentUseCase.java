package es.us.ieal.search.domain.port.in;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.entity.ContentPage;
import es.us.ieal.search.domain.entity.SearchPage;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.valueobject.SearchFilters;
import es.us.ieal.search.domain.valueobject.SearchQuery;

import java.util.List;

public interface ContentUseCase {
    ContentItem create(ContentItem content);
    List<ContentItem> createBulk(List<ContentItem> contents);
    ContentItem getById(String id);
    ContentItem update(String id, ContentItem content);
    ContentItem reindex(String id);
    ContentPage list(ContentType contentType, int page, int size);
    SearchPage search(SearchQuery query, SearchFilters filters);
}
