package tech.vanguardops.vanguardops.maintenancerule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MaintenanceRuleNotFoundException extends RuntimeException {
    public MaintenanceRuleNotFoundException(String message) {
        super(message);
    }
}