package tech.vanguardops.vanguardops.aircraft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.vanguardops.vanguardops.aircraft.exception.AircraftDataConflictException;
import tech.vanguardops.vanguardops.aircraft.exception.AircraftNotFoundException;

import java.util.List;

@Service("aircraftServiceV1")
@RequiredArgsConstructor
public class AircraftServiceV1 implements AircraftService {
    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;

    @Override
    @Transactional
    public Aircraft create(Aircraft aircraft) {
        validateUniqueTailAndSerial(aircraft.getTailNumber(), aircraft.getSerialNumber(), null);
        return aircraftRepository.save(aircraft);
    }

    @Override
    @Transactional
    public Aircraft update(Long id, Aircraft aircraft) {
        validateUniqueTailAndSerial(aircraft.getTailNumber(), aircraft.getSerialNumber(), id);
        var existingAircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft with ID " + id + " not found"));

        aircraft.setId(id);

        // Update fields
        aircraftMapper.updateEntity(aircraft, existingAircraft);
        return aircraftRepository.save(existingAircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Aircraft> getAll() {
        return aircraftRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Aircraft getById(Long id) throws AircraftNotFoundException {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("Aircraft with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!aircraftRepository.existsById(id)) {
            throw new AircraftNotFoundException("Aircraft with ID " + id + " not found");
        }
        aircraftRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Aircraft setStatus(Long id, AircraftStatus status) {
        var aircraft = getById(id);
        aircraft.setStatus(status);
        return aircraftRepository.save(aircraft);
    }

    /**
     * Validate that the tail number and serial number are unique.
     * @param tail the tail
     * @param serial the serial number
     * @param id the id
     */
    private void validateUniqueTailAndSerial(String tail, String serial, Long id) {
        if (id == null) { // create
            if (aircraftRepository.existsByTailNumberIgnoreCase(tail))
                throw new AircraftDataConflictException("Tail number exists");
            if (aircraftRepository.existsBySerialNumberIgnoreCase(serial))
                throw new AircraftDataConflictException("Serial number exists");
        } else { // update
            if (aircraftRepository.existsByTailNumberIgnoreCaseAndIdNot(tail, id))
                throw new AircraftDataConflictException("Tail number exists");
            if (aircraftRepository.existsBySerialNumberIgnoreCaseAndIdNot(serial, id))
                throw new AircraftDataConflictException("Serial number exists");
        }
    }
}