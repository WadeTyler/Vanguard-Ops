package tech.vanguardops.vanguardops.maintenancerule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import tech.vanguardops.vanguardops.maintenancerule.MaintenanceRule;
import tech.vanguardops.vanguardops.maintenancerule.dto.MaintenanceRuleDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaintenanceRuleMapper {

    MaintenanceRule toEntity(MaintenanceRuleDTO dto);

    MaintenanceRuleDTO toDTO(MaintenanceRule entity);
}