package tech.vanguardops.vanguardops.aircraft.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AircraftDataConflictException extends RuntimeException {
    public AircraftDataConflictException(String message) {
        super(message);
    }
}