package es.us.ieal.search.domain.exception;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(String id) {
        super("No existe el contenido con id " + id);
    }
}
