package es.us.ieal.search.boot.config;

import es.us.ieal.search.application.service.ContentService;
import es.us.ieal.search.domain.port.in.ContentUseCase;
import es.us.ieal.search.domain.port.out.ContentRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    ContentUseCase contentUseCase(ContentRepositoryPort repository) {
        return new ContentService(repository);
    }
}
