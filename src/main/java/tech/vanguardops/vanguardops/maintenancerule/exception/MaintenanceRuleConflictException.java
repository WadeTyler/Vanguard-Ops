package tech.vanguardops.vanguardops.maintenancerule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MaintenanceRuleConflictException extends RuntimeException {
    public MaintenanceRuleConflictException(String message) {
        super(message);
    }
}