package tech.vanguardops.vanguardops.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.vanguardops.vanguardops.common.ErrorResponse;
import tech.vanguardops.vanguardops.config.AppProperties;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalRestControllerAdvice {

    private final AppProperties appProperties;

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        logException(ex);
        return buildResponseEntity(new RuntimeException("You do not have permission to perform this action."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logException(ex);
        return buildResponseEntity(new RuntimeException("Database error: " + ex.getMostSpecificCause().getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logException(ex);
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(". "));
        return buildResponseEntity(new RuntimeException(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logException(ex);
        return buildResponseEntity(new RuntimeException("Invalid or missing request body."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logException(ex);
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        logException(ex);
        return buildResponseEntity(ex);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(Exception ex, HttpStatus status) {
        ErrorResponse errorResponse = buildErrorResponse(ex, status);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(Exception ex) {
        HttpStatus status = extractStatus(ex);
        return buildResponseEntity(ex, status);
    }

    private ErrorResponse buildErrorResponse(Exception ex, HttpStatus status) {
        return new ErrorResponse(
                ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                LocalDateTime.now()
        );
    }

    private HttpStatus extractStatus(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Check for @ResponseStatus annotation
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.value();
        }

        return status;
    }

    private void logException(Exception ex) {
        if (!appProperties.isProduction()) {
            log.error("Exception: ", ex);
        }

    }

}