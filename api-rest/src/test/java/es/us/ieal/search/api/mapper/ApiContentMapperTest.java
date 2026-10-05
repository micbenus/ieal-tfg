package es.us.ieal.search.api.mapper;

import es.us.ieal.search.generated.model.ContentStatus;
import es.us.ieal.search.generated.model.ContentType;
import es.us.ieal.search.generated.model.CreateContentRequest;
import es.us.ieal.search.generated.model.SourceSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiContentMapperTest {
    @Test
    void convierteElContratoEnDominio() {
        CreateContentRequest request = new CreateContentRequest();
        request.setSourceId("event-1");
        request.setSourceSystem(SourceSystem.MANUAL);
        request.setSlug("jornadas");
        request.setTitle("Jornadas americanistas");
        request.setContentType(ContentType.EVENT);
        request.setLanguage("es");
        request.setStatus(ContentStatus.INDEXED);
        request.setVisible(true);

        var domain = ApiContentMapper.toDomain(request);

        assertEquals(es.us.ieal.search.domain.enums.ContentType.EVENT, domain.contentType());
        assertEquals("Jornadas americanistas", domain.title());
    }
}
