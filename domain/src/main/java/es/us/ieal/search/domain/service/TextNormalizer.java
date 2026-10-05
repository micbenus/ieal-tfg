package es.us.ieal.search.domain.service;

import es.us.ieal.search.domain.entity.ContentItem;

import java.text.Normalizer;
import java.util.Locale;
import java.util.stream.Stream;

public final class TextNormalizer {
    private TextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) return "";
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    public static String searchableText(ContentItem item) {
        return normalize(Stream.of(
                        item.title(),
                        item.summary(),
                        item.body(),
                        String.join(" ", item.keywords()),
                        String.join(" ", item.categories()),
                        String.join(" ", item.tags()))
                .reduce("", (left, right) -> left + " " + right));
    }
}
