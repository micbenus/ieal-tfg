package es.us.ieal.search.domain.port.out;

import es.us.ieal.search.domain.entity.ContentItem;

import java.util.List;
import java.util.Optional;

public interface ContentRepositoryPort {
    ContentItem save(ContentItem content);
    Optional<ContentItem> findById(String id);
    boolean existsBySourceSystemAndSourceId(String sourceSystem, String sourceId);
    List<ContentItem> findAll();
    List<ContentItem> findAllVisible();
}
