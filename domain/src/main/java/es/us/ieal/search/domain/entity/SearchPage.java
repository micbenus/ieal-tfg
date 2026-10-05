package es.us.ieal.search.domain.entity;

import java.util.List;

public record SearchPage(long total, int page, int size, List<ScoredContent> results) {
    public SearchPage {
        results = List.copyOf(results);
    }
}
