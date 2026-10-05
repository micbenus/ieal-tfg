package es.us.ieal.search.api.controller;

import es.us.ieal.search.api.mapper.ApiContentMapper;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import es.us.ieal.search.domain.valueobject.SearchFilters;
import es.us.ieal.search.domain.valueobject.SearchQuery;
import es.us.ieal.search.generated.api.SearchApi;
import es.us.ieal.search.generated.model.SearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController implements SearchApi {
    private final ContentUseCase useCase;

    public SearchController(ContentUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<SearchResponse> searchContents(
            String q,
            es.us.ieal.search.generated.model.ContentType type,
            String section,
            String category,
            Integer page,
            Integer size,
            String sort) {
        ContentType domainType = type == null ? null : ContentType.valueOf(type.getValue());
        int requestedPage = page == null ? 0 : page;
        int requestedSize = size == null ? 20 : size;
        String requestedSort = sort == null ? "relevance" : sort;
        SearchFilters.SortOrder sortOrder = "date".equalsIgnoreCase(requestedSort)
                ? SearchFilters.SortOrder.DATE
                : SearchFilters.SortOrder.RELEVANCE;

        var result = useCase.search(new SearchQuery(q),
                new SearchFilters(domainType, section, category, requestedPage,
                        requestedSize, sortOrder));
        return ResponseEntity.ok(ApiContentMapper.toSearchResponse(
                result, q, domainType, section, category, requestedSort));
    }
}
