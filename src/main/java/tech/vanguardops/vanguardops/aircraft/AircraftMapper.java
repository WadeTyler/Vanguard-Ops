package tech.vanguardops.vanguardops.aircraft;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import tech.vanguardops.vanguardops.aircraft.dto.AircraftDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AircraftMapper {
    AircraftDTO toDTO(Aircraft aircraft);
    Aircraft toEntity(AircraftDTO aircraftDTO);
    void updateEntity(Aircraft incomingUpdates, @MappingTarget Aircraft existingAircraft);
}