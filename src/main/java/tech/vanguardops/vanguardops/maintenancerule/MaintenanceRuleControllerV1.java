package tech.vanguardops.vanguardops.maintenancerule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.vanguardops.vanguardops.maintenancerule.dto.ActiveRuleDTO;
import tech.vanguardops.vanguardops.maintenancerule.dto.MaintenanceRuleDTO;
import tech.vanguardops.vanguardops.maintenancerule.mapper.ActiveRuleMapper;
import tech.vanguardops.vanguardops.maintenancerule.mapper.MaintenanceRuleMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance-rules")
public class MaintenanceRuleControllerV1 {
    private final MaintenanceRuleService maintenanceRuleService;
    private final MaintenanceRuleMapper maintenanceRuleMapper;
    private final ActiveRuleMapper activeRuleMapper;

    public MaintenanceRuleControllerV1(@Qualifier("maintenanceRuleServiceV1") MaintenanceRuleService maintenanceRuleService, MaintenanceRuleMapper maintenanceRuleMapper, ActiveRuleMapper activeRuleMapper) {
        this.maintenanceRuleService = maintenanceRuleService;
        this.maintenanceRuleMapper = maintenanceRuleMapper;
        this.activeRuleMapper = activeRuleMapper;
    }

    /// Maintenance Rule Endpoints ///

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MaintenanceRuleDTO getMaintenanceRule(Long id) {
        return maintenanceRuleMapper.toDTO(maintenanceRuleService.getMaintenanceRule(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MaintenanceRuleDTO> listMaintenanceRules(Pageable pageable) {
        return maintenanceRuleService.listMaintenanceRules(pageable).map(maintenanceRuleMapper::toDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceRuleDTO createMaintenanceRule(MaintenanceRuleDTO createRequest) {
        MaintenanceRule maintenanceRule = maintenanceRuleMapper.toEntity(createRequest);
        return maintenanceRuleMapper.toDTO(maintenanceRuleService.createMaintenanceRule(maintenanceRule));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MaintenanceRuleDTO updateMaintenanceRule(@PathVariable Long id, @RequestBody MaintenanceRuleDTO updateRequest) {
        MaintenanceRule maintenanceRule = maintenanceRuleMapper.toEntity(updateRequest);
        return maintenanceRuleMapper.toDTO(maintenanceRuleService.updateMaintenanceRule(id, maintenanceRule));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaintenanceRule(@PathVariable Long id) {
        maintenanceRuleService.deleteMaintenanceRule(id);
    }

    /// Active Rule Endpoints ///

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public Page<ActiveRuleDTO> listActiveRules(Pageable pageable) {
        return maintenanceRuleService.listActiveRules(pageable).map(activeRuleMapper::toDTO);
    }

    @GetMapping("/active/{aircraftId}/{ruleId}")
    @ResponseStatus(HttpStatus.OK)
    public ActiveRuleDTO getActiveRule(@PathVariable Long aircraftId, @PathVariable Long ruleId) {
        return activeRuleMapper.toDTO(maintenanceRuleService.getActiveRule(aircraftId, ruleId));
    }

    @GetMapping("/active/aircraft/{aircraftId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ActiveRuleDTO> getAllByAircraftId(@PathVariable Long aircraftId) {
        return maintenanceRuleService.getAllByAircraftId(aircraftId)
                .stream().map(activeRuleMapper::toDTO).toList();
    }

    @GetMapping("/active/rule/{ruleId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ActiveRuleDTO> getAllByRuleId(@PathVariable Long ruleId) {
        return maintenanceRuleService.getAllByRuleId(ruleId)
                .stream().map(activeRuleMapper::toDTO).toList();
    }

    @PostMapping("/active")
    @ResponseStatus(HttpStatus.CREATED)
    public ActiveRuleDTO activateRule(@RequestParam Long aircraftId, @RequestParam Long ruleId) {
        return activeRuleMapper.toDTO(maintenanceRuleService.activateRule(aircraftId, ruleId));
    }

    @DeleteMapping("/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateRule(@RequestParam Long aircraftId, @RequestParam Long ruleId) {
        maintenanceRuleService.deactivateRule(aircraftId, ruleId);
    }

}