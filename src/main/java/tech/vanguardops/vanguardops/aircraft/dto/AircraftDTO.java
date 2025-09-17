package tech.vanguardops.vanguardops.aircraft.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import tech.vanguardops.vanguardops.aircraft.AircraftStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AircraftDTO {

    private Long id;

    @NotBlank
    @Size(max = 20)
    private String tailNumber;

    @NotBlank
    @Size(max = 50)
    private String model;

    @Size(max = 50)
    private String variant;

    @NotBlank
    @Size(max = 100)
    private String manufacturer;

    @NotBlank
    @Size(max = 100)
    private String serialNumber;

    @DecimalMin("0.0")
    private BigDecimal totalFlightHours;

    @Min(0)
    private Integer totalCycles;

    private LocalDateTime lastFlightDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AircraftStatus status;

    @Size(max = 50)
    private String squadron;

    @Size(max = 50)
    private String baseLocation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}