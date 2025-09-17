package tech.vanguardops.vanguardops.aircraft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    boolean existsByTailNumberIgnoreCase(String tailNumber);

    boolean existsByTailNumberIgnoreCaseAndIdNot(String tailNumber, Long id);

    boolean existsBySerialNumberIgnoreCase(String serialNumber);

    boolean existsBySerialNumberIgnoreCaseAndIdNot(String serialNumber, Long id);
}