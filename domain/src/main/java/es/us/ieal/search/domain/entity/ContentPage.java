package es.us.ieal.search.domain.entity;

import java.util.List;

public record ContentPage(long total, int page, int size, List<ContentItem> items) {
    public ContentPage {
        items = List.copyOf(items);
    }
}
