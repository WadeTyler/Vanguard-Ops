package tech.vanguardops.vanguardops.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,

        String username,
        String firstName,
        String lastName,
        String authority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}