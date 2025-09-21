package tech.vanguardops.vanguardops.maintenance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingOrderException extends RuntimeException {
    public ExistingOrderException(String message) {
        super(message);
    }
}