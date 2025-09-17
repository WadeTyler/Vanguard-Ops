package tech.vanguardops.vanguardops.common;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String error,
        int status,
        LocalDateTime timestamp
) {
}