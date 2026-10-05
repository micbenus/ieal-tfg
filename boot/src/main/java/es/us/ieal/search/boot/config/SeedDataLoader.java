package es.us.ieal.search.boot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import es.us.ieal.search.domain.exception.DuplicateContentException;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@Profile("dev")
public class SeedDataLoader implements ApplicationRunner {
    private final ObjectMapper objectMapper;
    private final ContentUseCase useCase;

    public SeedDataLoader(ObjectMapper objectMapper, ContentUseCase useCase) {
        this.objectMapper = objectMapper;
        this.useCase = useCase;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (InputStream input = new ClassPathResource("data/seed-contents.json").getInputStream()) {
            List<SeedContent> seeds = objectMapper.readValue(input, new TypeReference<>() {});
            for (SeedContent seed : seeds) {
                try {
                    useCase.create(seed.toDomain());
                } catch (DuplicateContentException ignored) {
                    // Permite reiniciar la demo sin duplicar los datos iniciales.
                }
            }
        }
    }

    private record SeedContent(
            String sourceId,
            SourceSystem sourceSystem,
            String sourceUrl,
            String slug,
            String title,
            String summary,
            String body,
            ContentType contentType,
            String section,
            List<String> categories,
            List<String> tags,
            List<String> keywords,
            String language,
            OffsetDateTime publishedAt,
            OffsetDateTime eventStartAt,
            OffsetDateTime eventEndAt,
            String location,
            String organizer,
            ContentStatus status,
            boolean visible
    ) {
        ContentItem toDomain() {
            return new ContentItem(null, sourceId, sourceSystem, sourceUrl, slug, title,
                    summary, body, contentType, section, categories, tags, keywords,
                    language, publishedAt, publishedAt, null, eventStartAt, eventEndAt,
                    location, organizer, status, visible, "");
        }
    }
}
