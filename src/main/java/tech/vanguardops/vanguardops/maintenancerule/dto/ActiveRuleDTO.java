package tech.vanguardops.vanguardops.maintenancerule.dto;

import tech.vanguardops.vanguardops.aircraft.dto.AircraftDTO;

import java.time.LocalDateTime;

public record ActiveRuleDTO(
        AircraftDTO aircraft,
        MaintenanceRuleDTO rule,
        LocalDateTime lastTriggered,
        Integer lastValue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}