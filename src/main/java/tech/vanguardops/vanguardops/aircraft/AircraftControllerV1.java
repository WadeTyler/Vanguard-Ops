package tech.vanguardops.vanguardops.aircraft;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.vanguardops.vanguardops.aircraft.dto.AircraftDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aircraft")
public class AircraftControllerV1 {

    private final AircraftService aircraftService;
    private final AircraftMapper aircraftMapper;

    public AircraftControllerV1(@Qualifier("aircraftServiceV1") AircraftService aircraftService,
                                AircraftMapper aircraftMapper) {
        this.aircraftService = aircraftService;
        this.aircraftMapper = aircraftMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AircraftDTO createAircraft(@RequestBody @Valid AircraftDTO aircraftDTO) {
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDTO);
        Aircraft savedAircraft = aircraftService.create(aircraft);
        return aircraftMapper.toDTO(savedAircraft);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public AircraftDTO updateAircraft(@PathVariable Long id, @RequestBody @Valid AircraftDTO aircraftDTO) {
        Aircraft updatedAircraft = aircraftService.update(id, aircraftMapper.toEntity(aircraftDTO));
        return aircraftMapper.toDTO(updatedAircraft);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR', 'PLANNER', 'ANALYST')")
    public List<AircraftDTO> getAllAircraft() {
        return aircraftService.getAll()
                .stream()
                .map(aircraftMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'OPERATOR', 'PLANNER', 'ANALYST')")
    public AircraftDTO getAircraftById(@PathVariable Long id) {
        Aircraft aircraft = aircraftService.getById(id);
        return aircraftMapper.toDTO(aircraft);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAircraft(@PathVariable Long id) {
        aircraftService.delete(id);
    }
}