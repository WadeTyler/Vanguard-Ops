package tech.vanguardops.vanguardops.maintenance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MaintenaceOrderStatusChangeException extends RuntimeException {
    public MaintenaceOrderStatusChangeException(String message) {
        super(message);
    }
}