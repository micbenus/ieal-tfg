package es.us.ieal.search.domain.exception;

public class DuplicateContentException extends RuntimeException {
    public DuplicateContentException(String sourceId) {
        super("Ya existe un contenido con sourceId " + sourceId);
    }
}
