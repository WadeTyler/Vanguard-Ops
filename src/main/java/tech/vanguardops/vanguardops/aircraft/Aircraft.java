package tech.vanguardops.vanguardops.aircraft;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft")
@Data
@AllArgsConstructor @NoArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class Aircraft {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String tailNumber;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(length = 50)
    private String variant;

    @Column(nullable = false, length = 100)
    private String manufacturer;

    @Column(nullable = false, length = 100, unique = true)
    private String serialNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalFlightHours = BigDecimal.ZERO;

    private Integer totalCycles = 0;

    private LocalDateTime lastFlightDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AircraftStatus status;

    @Column(length = 50)
    private String squadron;

    @Column(length = 50)
    private String baseLocation;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}