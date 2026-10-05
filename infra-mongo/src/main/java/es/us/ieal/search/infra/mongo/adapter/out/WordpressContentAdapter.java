package es.us.ieal.search.infra.mongo.adapter.out;

import es.us.ieal.search.domain.entity.ContentItem;
import es.us.ieal.search.domain.port.out.ExternalContentProviderPort;

import java.util.List;

/**
 * Extensión prevista para WordPress REST API.
 * No se registra como bean porque la integración queda fuera del alcance del MVP.
 */
public class WordpressContentAdapter implements ExternalContentProviderPort {
    @Override
    public List<ContentItem> fetchContents() {
        throw new UnsupportedOperationException("Integración WordPress no implementada en el MVP");
    }
}
