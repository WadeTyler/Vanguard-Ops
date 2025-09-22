package tech.vanguardops.vanguardops.maintenancerule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import tech.vanguardops.vanguardops.aircraft.AircraftMapper;
import tech.vanguardops.vanguardops.maintenancerule.ActiveRule;
import tech.vanguardops.vanguardops.maintenancerule.dto.ActiveRuleDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {MaintenanceRuleMapper.class, AircraftMapper.class})
public interface ActiveRuleMapper {

    ActiveRuleDTO toDTO(ActiveRule activeRule);
}