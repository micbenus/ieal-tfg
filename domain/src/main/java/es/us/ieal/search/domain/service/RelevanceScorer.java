package es.us.ieal.search.domain.service;

import es.us.ieal.search.domain.entity.ContentItem;

public final class RelevanceScorer {
    private RelevanceScorer() {
    }

    public static double score(ContentItem item, String rawQuery) {
        String query = TextNormalizer.normalize(rawQuery);
        double score = 0;
        score += contains(item.title(), query) ? 5 : 0;
        score += contains(item.summary(), query) ? 3 : 0;
        score += contains(String.join(" ", item.keywords()), query) ? 3 : 0;
        score += contains(String.join(" ", item.categories()), query) ? 2 : 0;
        score += contains(item.body(), query) ? 1 : 0;
        return score;
    }

    private static boolean contains(String value, String query) {
        return TextNormalizer.normalize(value).contains(query);
    }
}
