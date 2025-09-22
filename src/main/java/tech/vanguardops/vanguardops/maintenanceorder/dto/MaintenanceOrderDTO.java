package tech.vanguardops.vanguardops.maintenanceorder.dto;

import tech.vanguardops.vanguardops.auth.dto.UserDTO;
import tech.vanguardops.vanguardops.maintenanceorder.OrderStatus;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for MaintenanceOrder
 */
public record MaintenanceOrderDTO(
        Long id,
        Long aircraftId,
        Integer priority,
        OrderStatus status,
        String description,
        UserDTO placedBy,
        UserDTO completedBy,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}