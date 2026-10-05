package es.us.ieal.search.infra.mongo.repository;

import es.us.ieal.search.infra.mongo.entity.ContentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataContentRepository extends MongoRepository<ContentDocument, String> {
    boolean existsBySourceSystemAndSourceId(String sourceSystem, String sourceId);
    List<ContentDocument> findAllByVisibleTrue();
}
