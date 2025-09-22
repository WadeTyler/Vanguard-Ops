package tech.vanguardops.vanguardops.maintenanceorder;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tech.vanguardops.vanguardops.aircraft.Aircraft;
import tech.vanguardops.vanguardops.auth.User;

import java.time.LocalDateTime;

/*
 * Entity representing a maintenance order for an aircraft.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "maintenance_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "placed_by")
    private User placedBy;

    @ManyToOne
    @JoinColumn(name = "completed_by")
    private User completedBy;

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}