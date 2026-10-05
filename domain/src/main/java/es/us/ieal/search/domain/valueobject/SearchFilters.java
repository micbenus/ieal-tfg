package es.us.ieal.search.domain.valueobject;

import es.us.ieal.search.domain.enums.ContentType;

public record SearchFilters(
        ContentType contentType,
        String section,
        String category,
        int page,
        int size,
        SortOrder sortOrder
) {
    public enum SortOrder { RELEVANCE, DATE }

    public SearchFilters {
        if (page < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");
        }
        sortOrder = sortOrder == null ? SortOrder.RELEVANCE : sortOrder;
    }
}
