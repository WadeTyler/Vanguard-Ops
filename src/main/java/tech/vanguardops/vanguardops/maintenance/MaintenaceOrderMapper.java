package tech.vanguardops.vanguardops.maintenance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tech.vanguardops.vanguardops.auth.UserMapper;
import tech.vanguardops.vanguardops.maintenance.dto.MaintenanceOrderDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface MaintenaceOrderMapper {

    @Mapping(source = "aircraft.id", target = "aircraftId")
    MaintenanceOrderDTO toDTO(MaintenanceOrder maintenanceOrder);
}