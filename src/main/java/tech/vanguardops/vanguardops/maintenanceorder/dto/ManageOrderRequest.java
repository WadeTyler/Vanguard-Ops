package tech.vanguardops.vanguardops.maintenanceorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request object for creating or updating a MaintenanceOrder
 */
public record ManageOrderRequest(
        @NotNull
        Long aircraftId,

        @NotBlank
        String description,

        @NotNull
        @PositiveOrZero
        Integer priority
) {
}