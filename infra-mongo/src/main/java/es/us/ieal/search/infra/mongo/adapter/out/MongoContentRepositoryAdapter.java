package es.us.ieal.search.infra.mongo.adapter.out;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.port.out.ContentRepositoryPort;
import es.us.ieal.search.infra.mongo.mapper.MongoContentMapper;
import es.us.ieal.search.infra.mongo.repository.SpringDataContentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoContentRepositoryAdapter implements ContentRepositoryPort {
    private final SpringDataContentRepository repository;

    public MongoContentRepositoryAdapter(SpringDataContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContentItem save(ContentItem content) {
        return MongoContentMapper.toDomain(repository.save(MongoContentMapper.toDocument(content)));
    }

    @Override
    public Optional<ContentItem> findById(String id) {
        return repository.findById(id).map(MongoContentMapper::toDomain);
    }

    @Override
    public boolean existsBySourceSystemAndSourceId(String sourceSystem, String sourceId) {
        return repository.existsBySourceSystemAndSourceId(sourceSystem, sourceId);
    }

    @Override
    public List<ContentItem> findAll() {
        return repository.findAll().stream()
                .map(MongoContentMapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentItem> findAllVisible() {
        return repository.findAllByVisibleTrue().stream()
                .map(MongoContentMapper::toDomain)
                .toList();
    }
}
