package tech.vanguardops.vanguardops.maintenancerule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ActiveRuleConflictException extends RuntimeException {
    public ActiveRuleConflictException(String message) {
        super(message);
    }
}