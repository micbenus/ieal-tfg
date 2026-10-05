package es.us.ieal.search.domain.port.out;

import es.us.ieal.search.domain.entity.ContentItem;

import java.util.List;

/**
 * Punto de extensión para importar contenido desde WordPress u otro proveedor.
 * El MVP carga JSON local; una futura implementación REST no cambia el dominio.
 */
public interface ExternalContentProviderPort {
    List<ContentItem> fetchContents();
}
