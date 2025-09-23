package tech.vanguardops.vanguardops.maintenancerule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.vanguardops.vanguardops.maintenanceorder.MaintenanceService;
import tech.vanguardops.vanguardops.maintenanceorder.dto.ManageOrderRequest;
import tech.vanguardops.vanguardops.maintenanceorder.exception.ExistingOrderException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ActiveRuleAutoHandler {

    private final MaintenanceRuleService maintenanceRuleService;
    private final MaintenanceService maintenanceService;
    private final ActiveRuleRepository activeRuleRepository;

    public ActiveRuleAutoHandler(@Qualifier("maintenanceRuleServiceV1") MaintenanceRuleService maintenanceRuleService,
                                 @Qualifier("maintenanceServiceV1") MaintenanceService maintenanceService, ActiveRuleRepository activeRuleRepository) {
        this.maintenanceRuleService = maintenanceRuleService;
        this.maintenanceService = maintenanceService;
        this.activeRuleRepository = activeRuleRepository;
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    private void handleActiveRules() {
        log.info("Starting active maintenance rule processing...");
        var now = LocalDateTime.now();
        // Get all active rules
        List<ActiveRule> allActiveRules = maintenanceRuleService.getAllActiveRules();
        // Filter out the ones that need to be processed
        List<ActiveRule> toProcess = allActiveRules.stream()
                .filter(rule -> {
                    RuleType ruleType = rule.getRule().getRuleType();
                    int threshold = rule.getRule().getThresholdValue();
                    switch (ruleType) {
                        case RuleType.DAYS:
                            if (rule.getLastTriggered() == null) {
                                return true; // Never triggered before, so process it
                            }
                            return rule.getLastTriggered().isBefore(now.minusDays(rule.getRule().getThresholdValue()));
                        case RuleType.CYCLES:
                            var cycles = rule.getAircraft().getTotalCycles();
                            if (rule.getLastValue() == null) {
                                return cycles >= threshold;
                            } else {
                                return (cycles - rule.getLastValue()) >= threshold;
                            }
                        case RuleType.FLIGHT_HOURS:
                            var hours = rule.getAircraft().getTotalFlightHours();
                            if (rule.getLastValue() == null) {
                                return hours.intValue() >= threshold;
                            } else {
                                return (hours.intValue() - rule.getLastValue()) >= threshold;
                            }
                        default:
                            return false;
                    }
                }).toList();

        // Place a maintenance order for each rule
        // TODO: Move to batch processing if this becomes a bottleneck
        int prexistingOrders = 0;
        for (ActiveRule rule : toProcess) {
            try {
                var aircraftId = rule.getAircraft().getId();
                var description = "System - Based on rule: " + rule.getRule().getName() + ". " + rule.getRule().getDescription();
                var manageOrderRequest = new ManageOrderRequest(aircraftId, description, 1);
                maintenanceService.placeOrder(manageOrderRequest, null);
            } catch (ExistingOrderException e) {
                // An open order already exists for this aircraft, so we skip creating a new one
                prexistingOrders++;
            } finally {
                // Update the active rule's last triggered time and last value
                rule.setLastTriggered(now);
                if (rule.getRule().getRuleType() == RuleType.CYCLES) {
                    rule.setLastValue(rule.getAircraft().getTotalCycles());
                } else if (rule.getRule().getRuleType() == RuleType.FLIGHT_HOURS) {
                    rule.setLastValue(rule.getAircraft().getTotalFlightHours().intValue());
                }
            }
        }
        // Save all updated rules
        activeRuleRepository.saveAll(toProcess);
        log.info("Processed {} active maintenance rules at {}", toProcess.size(), now);
        if (prexistingOrders > 0) {
            log.info("Skipped creating {} maintenance orders due to pre-existing open orders.", prexistingOrders);
        }
    }
}