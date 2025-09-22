package tech.vanguardops.vanguardops.maintenancerule.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tech.vanguardops.vanguardops.maintenancerule.RuleType;

import java.time.LocalDateTime;

public record MaintenanceRuleDTO(
        Long id,

        @NotBlank
        @Size(max = 255)
        String name,

        String description,

        @NotNull
        RuleType ruleType,

        @NotNull
        @Min(0)
        Integer thresholdValue,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}