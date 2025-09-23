package tech.vanguardops.vanguardops.maintenanceorder;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tech.vanguardops.vanguardops.auth.UserMapper;
import tech.vanguardops.vanguardops.maintenanceorder.dto.MaintenanceOrderDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface MaintenanceOrderMapper {

    @Mapping(source = "aircraft.id", target = "aircraftId")
    MaintenanceOrderDTO toDTO(MaintenanceOrder maintenanceOrder);
}