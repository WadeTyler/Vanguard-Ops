package tech.vanguardops.vanguardops.maintenancerule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.vanguardops.vanguardops.aircraft.AircraftService;
import tech.vanguardops.vanguardops.maintenancerule.exception.ActiveRuleConflictException;
import tech.vanguardops.vanguardops.maintenancerule.exception.ActiveRuleNotFoundException;
import tech.vanguardops.vanguardops.maintenancerule.exception.MaintenanceRuleConflictException;
import tech.vanguardops.vanguardops.maintenancerule.exception.MaintenanceRuleNotFoundException;

import java.util.List;

@Service(value = "maintenanceRuleServiceV1")
public class MaintenanceRuleServiceV1 implements MaintenanceRuleService {

    private final MaintenanceRuleRepository maintenanceRuleRepository;
    private final ActiveRuleRepository activeRuleRepository;
    private final AircraftService aircraftService;

    public MaintenanceRuleServiceV1(MaintenanceRuleRepository maintenanceRuleRepository, ActiveRuleRepository activeRuleRepository,
                                    @Qualifier("aircraftServiceV1") AircraftService aircraftService) {
        this.maintenanceRuleRepository = maintenanceRuleRepository;
        this.activeRuleRepository = activeRuleRepository;
        this.aircraftService = aircraftService;
    }

    @Transactional
    @Override
    public MaintenanceRule getMaintenanceRule(Long id) throws MaintenanceRuleNotFoundException {
        return maintenanceRuleRepository.findById(id)
                .orElseThrow(() -> new MaintenanceRuleNotFoundException("Maintenance rule not found."));
    }

    @Transactional
    @Override
    public Page<MaintenanceRule> listMaintenanceRules(Pageable pageable) {
        return maintenanceRuleRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public MaintenanceRule createMaintenanceRule(MaintenanceRule createRequest) throws MaintenanceRuleConflictException, IllegalArgumentException {
        // Check if rule exists by name
        if (maintenanceRuleRepository.existsByNameEquals(createRequest.getName())) {
            throw new MaintenanceRuleConflictException("A maintenance rule with this name already exists.");
        }

        // Validate requirement value
        if (createRequest.getThresholdValue() <= 0) {
            throw new IllegalArgumentException("Requirement value must be greater than zero.");
        }

        // Save
        return maintenanceRuleRepository.save(createRequest);
    }

    @Transactional
    @Override
    public MaintenanceRule updateMaintenanceRule(Long id, MaintenanceRule updateRequest) {
        // Check if rule exists
        var rule = getMaintenanceRule(id);

        // Check if name is changing and if new name already exists
        if (!rule.getName().equals(updateRequest.getName()) && maintenanceRuleRepository.existsByNameEqualsAndIdNot(updateRequest.getName(), id)) {
            throw new MaintenanceRuleConflictException("A maintenance rule with this name already exists.");
        }

        // Validate requirement value
        if (updateRequest.getThresholdValue() <= 0) {
            throw new IllegalArgumentException("Requirement value must be greater than zero.");
        }

        // Update fields
        rule.setName(updateRequest.getName());
        rule.setDescription(updateRequest.getDescription());
        rule.setRuleType(updateRequest.getRuleType());
        rule.setThresholdValue(updateRequest.getThresholdValue());

        return maintenanceRuleRepository.save(rule);
    }

    @Transactional
    @Override
    public void deleteMaintenanceRule(Long id) {
        // Check if rule exists
        if (!maintenanceRuleRepository.existsById(id)) {
            throw new MaintenanceRuleNotFoundException("Maintenance rule not found.");
        }
        // Check if rule is active on any aircraft
        if (activeRuleRepository.existsByRule_Id(id)) {
            throw new MaintenanceRuleConflictException("Cannot delete a maintenance rule that is active on aircraft.");
        }

        maintenanceRuleRepository.deleteById(id);
    }

    @Transactional
    @Override
    public List<ActiveRule> getAllByAircraftId(Long aircraftId) {
        return activeRuleRepository.findAllByAircraft_Id(aircraftId);
    }

    @Transactional
    @Override
    public List<ActiveRule> getAllByRuleId(Long ruleId) {
        return activeRuleRepository.findAllByRule_Id(ruleId);
    }

    @Transactional
    @Override
    public ActiveRule getActiveRule(Long aircraftId, Long ruleId) {
        return activeRuleRepository.findAllByAircraft_IdAndRule_Id(aircraftId, ruleId)
                .orElseThrow(() -> new ActiveRuleNotFoundException("Active maintenance rule not found for the given aircraft and rule IDs."));
    }

    @Transactional
    @Override
    public Page<ActiveRule> listActiveRules(Pageable pageable) {
        return activeRuleRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public ActiveRule activateRule(Long aircraftId, Long ruleId) {
        // Check if already active
        if (activeRuleRepository.existsByAircraft_IdAndRule_Id(aircraftId, ruleId)) {
            throw new ActiveRuleConflictException("This maintenance rule is already active for the specified aircraft.");
        }

        // Fetch aircraft and rule
        var aircraft = aircraftService.getById(aircraftId);
        var rule = getMaintenanceRule(ruleId);

        // Create and save active rule
        var activeRule = ActiveRule.builder()
                .aircraft(aircraft)
                .rule(rule)
                .lastTriggered(null)
                .lastValue(null)
                .build();

        return activeRuleRepository.save(activeRule);
    }

    @Transactional
    @Override
    public void deactivateRule(Long aircraftId, Long ruleId) {
        // Check if exists
        if (!activeRuleRepository.existsByAircraft_IdAndRule_Id(aircraftId, ruleId)) {
            throw new ActiveRuleNotFoundException("Active maintenance rule not found for the given aircraft and rule IDs.");
        }

        // Delete
        activeRuleRepository.deleteByAircraft_IdAndRule_Id(aircraftId, ruleId);
    }

    @Override
    public List<ActiveRule> getAllActiveRules() {
        return activeRuleRepository.findAll();
    }
}