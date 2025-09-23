package tech.vanguardops.vanguardops.maintenancerule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MaintenanceRuleService {

    // Maintenace Rules
    MaintenanceRule getMaintenanceRule(Long id);
    Page<MaintenanceRule> listMaintenanceRules(Pageable pageable);
    MaintenanceRule createMaintenanceRule(MaintenanceRule createRequest);
    MaintenanceRule updateMaintenanceRule(Long id, MaintenanceRule updateRequest);
    void deleteMaintenanceRule(Long id);

    // Active Rules
    List<ActiveRule> getAllByAircraftId(Long aircraftId);
    List<ActiveRule> getAllByRuleId(Long ruleId);
    ActiveRule getActiveRule(Long aircraftId, Long ruleId);
    Page<ActiveRule> listActiveRules(Pageable pageable);
    ActiveRule activateRule(Long aircraftId, Long ruleId);
    void deactivateRule(Long aircraftId, Long ruleId);
    List<ActiveRule> getAllActiveRules();
}