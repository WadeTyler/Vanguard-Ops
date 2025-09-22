package tech.vanguardops.vanguardops.maintenancerule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.vanguardops.vanguardops.aircraft.Aircraft;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "active_maintenance_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(ActiveRuleId.class)
public class ActiveRule {

    @Id
    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Id
    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private MaintenanceRule rule;

    private LocalDateTime lastTriggered;

    private Integer lastValue;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}