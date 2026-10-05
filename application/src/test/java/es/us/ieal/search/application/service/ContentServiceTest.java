package es.us.ieal.search.application.service;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.enums.ContentStatus;
import es.us.ieal.search.domain.enums.ContentType;
import es.us.ieal.search.domain.enums.SourceSystem;
import es.us.ieal.search.domain.exception.ContentNotFoundException;
import es.us.ieal.search.domain.port.out.ContentRepositoryPort;
import es.us.ieal.search.domain.valueobject.SearchFilters;
import es.us.ieal.search.domain.valueobject.SearchQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    @Mock
    private ContentRepositoryPort repository;
    private ContentService service;

    @BeforeEach
    void setUp() {
        service = new ContentService(repository);
    }

    @Test
    void creaContenidoNormalizado() {
        when(repository.existsBySourceSystemAndSourceId("MANUAL", "source-1")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ContentItem created = service.create(content(null, ContentType.PAGE, "Líneas de investigación"));

        assertTrue(created.searchTextNormalized().startsWith("lineas de investigacion"));
    }

    @Test
    void buscaYFiltraPorTipo() {
        when(repository.findAllVisible()).thenReturn(List.of(
                content("1", ContentType.PAGE, "Líneas de investigación"),
                content("2", ContentType.EVENT, "Jornadas de investigación")));

        var result = service.search(new SearchQuery("investigación"),
                new SearchFilters(ContentType.EVENT, null, null, 0, 20,
                        SearchFilters.SortOrder.RELEVANCE));

        assertEquals(1, result.total());
        assertEquals(ContentType.EVENT, result.results().get(0).content().contentType());
    }

    @Test
    void listaContenidosPaginadosPorTipo() {
        when(repository.findAll()).thenReturn(List.of(
                content("1", ContentType.PAGE, "El Instituto"),
                content("2", ContentType.EVENT, "Jornadas americanistas")));

        var result = service.list(ContentType.EVENT, 0, 20);

        assertEquals(1, result.total());
        assertEquals(ContentType.EVENT, result.items().get(0).contentType());
    }

    @Test
    void informaCuandoNoExisteElContenido() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> service.getById("missing"));
    }

    @Test
    void devuelvePaginaSolicitada() {
        when(repository.findAllVisible()).thenReturn(List.of(
                content("1", ContentType.PAGE, "América uno"),
                content("2", ContentType.PAGE, "América dos")));

        var result = service.search(new SearchQuery("america"),
                new SearchFilters(null, null, null, 1, 1,
                        SearchFilters.SortOrder.RELEVANCE));

        assertEquals(2, result.total());
        assertEquals(1, result.page());
        assertEquals(1, result.results().size());
    }

    private ContentItem content(String id, ContentType type, String title) {
        return new ContentItem(id, "source-" + (id == null ? "1" : id), SourceSystem.MANUAL,
                null, "slug-" + (id == null ? "1" : id), title, "", "", type,
                "Instituto", List.of("Investigación"), List.of(), List.of(), "es",
                OffsetDateTime.now(), OffsetDateTime.now(), null, null, null,
                null, null, ContentStatus.INDEXED, true, "");
    }
}
