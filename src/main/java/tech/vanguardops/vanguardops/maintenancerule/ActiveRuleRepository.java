package tech.vanguardops.vanguardops.maintenancerule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ActiveRuleRepository extends JpaRepository<ActiveRule, ActiveRuleId> {

    boolean existsByRule_Id(Long id);

    List<ActiveRule> findAllByAircraft_Id(Long aircraftId);

    List<ActiveRule> findAllByRule_Id(Long ruleId);

    Optional<ActiveRule> findAllByAircraft_IdAndRule_Id(Long aircraftId, Long ruleId);

    boolean existsByAircraft_IdAndRule_Id(Long aircraftId, Long ruleId);

    @Modifying
    @Transactional
    void deleteByAircraft_IdAndRule_Id(Long aircraftId, Long ruleId);
}