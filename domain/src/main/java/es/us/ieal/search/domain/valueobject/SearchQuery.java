package es.us.ieal.search.domain.valueobject;

public record SearchQuery(String value) {
    public SearchQuery {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La palabra clave de búsqueda es obligatoria");
        }
        value = value.trim();
    }
}
