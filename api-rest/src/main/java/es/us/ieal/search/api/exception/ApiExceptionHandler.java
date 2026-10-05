package es.us.ieal.search.api.exception;

import es.us.ieal.search.domain.exception.ContentNotFoundException;
import es.us.ieal.search.domain.exception.DuplicateContentException;
import es.us.ieal.search.generated.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ContentNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(ContentNotFoundException exception,
                                               HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateContentException.class)
    ResponseEntity<ApiErrorResponse> conflict(DuplicateContentException exception,
                                               HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class, ConstraintViolationException.class})
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Se ha producido un error interno", request);
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message,
                                                    HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse();
        body.setTimestamp(OffsetDateTime.now());
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
