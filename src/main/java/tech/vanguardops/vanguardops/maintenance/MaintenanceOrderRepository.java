package tech.vanguardops.vanguardops.maintenance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.vanguardops.vanguardops.aircraft.Aircraft;

/**
 * Repository interface for managing MaintenanceOrder entities.
 */
@Repository
public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {

    boolean existsByAircraftAndStatus(Aircraft aircraft, OrderStatus orderStatus);

    Page<MaintenanceOrder> findAllByAircraft(Aircraft aircraft, Pageable pageable);
}