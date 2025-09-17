package tech.vanguardops.vanguardops.aircraft.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AircraftNotFoundException extends RuntimeException{

    public AircraftNotFoundException(String message) {
        super(message);
    }
}