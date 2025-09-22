package tech.vanguardops.vanguardops.maintenanceorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MaintenanceOrderNotFoundException extends RuntimeException {
    public MaintenanceOrderNotFoundException(String message) {
        super(message);
    }
}