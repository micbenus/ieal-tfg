package es.us.ieal.search.api.controller;

import es.us.ieal.search.api.mapper.ApiContentMapper;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import es.us.ieal.search.generated.api.ContentsApi;
import es.us.ieal.search.generated.model.ContentPageResponse;
import es.us.ieal.search.generated.model.ContentResponse;
import es.us.ieal.search.generated.model.CreateContentRequest;
import es.us.ieal.search.generated.model.UpdateContentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ContentsController implements ContentsApi {
    private final ContentUseCase useCase;

    public ContentsController(ContentUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<ContentPageResponse> listContents(
            es.us.ieal.search.generated.model.ContentType type, Integer page, Integer size) {
        ContentType domainType = type == null ? null : ContentType.valueOf(type.getValue());
        return ResponseEntity.ok(ApiContentMapper.toPageResponse(
                useCase.list(domainType, page == null ? 0 : page, size == null ? 20 : size)));
    }

    @Override
    public ResponseEntity<ContentResponse> createContent(CreateContentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiContentMapper.toResponse(useCase.create(ApiContentMapper.toDomain(request))));
    }

    @Override
    public ResponseEntity<List<ContentResponse>> createContentsBulk(
            List<CreateContentRequest> requests) {
        var created = useCase.createBulk(requests.stream()
                .map(ApiContentMapper::toDomain)
                .toList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created.stream().map(ApiContentMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<ContentResponse> getContent(String id) {
        return ResponseEntity.ok(ApiContentMapper.toResponse(useCase.getById(id)));
    }

    @Override
    public ResponseEntity<ContentResponse> updateContent(
            String id, UpdateContentRequest request) {
        return ResponseEntity.ok(ApiContentMapper.toResponse(
                useCase.update(id, ApiContentMapper.toDomain(request))));
    }

    @Override
    public ResponseEntity<ContentResponse> reindexContent(String id) {
        return ResponseEntity.ok(ApiContentMapper.toResponse(useCase.reindex(id)));
    }
}
