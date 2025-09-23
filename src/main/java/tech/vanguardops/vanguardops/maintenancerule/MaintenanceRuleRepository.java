package tech.vanguardops.vanguardops.maintenancerule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRuleRepository extends JpaRepository<MaintenanceRule, Long> {
    boolean existsByNameEquals(String name);

    boolean existsByNameEqualsAndIdNot(String name, Long id);
}