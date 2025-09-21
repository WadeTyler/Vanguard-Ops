package tech.vanguardops.vanguardops.maintenance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.maintenance.dto.ManageOrderRequest;

/**
 * Service interface for managing maintenance orders.
 */
public interface MaintenanceService {

    /**
     * Place a new maintenance order.
     * @param request the order details
     * @param placedBy the user placing the order
     * @return the created MaintenanceOrder
     */
    MaintenanceOrder placeOrder(ManageOrderRequest request, User placedBy);

    /**
     * Update an existing maintenance order.
     * @param request the updated order details
     * @param orderId the ID of the order to update
     * @return the updated MaintenanceOrder
     */
    MaintenanceOrder updateOrder(ManageOrderRequest request, Long orderId);

    /**
     * Get a maintenance order by its ID.
     * @param orderId the ID of the order
     * @return the MaintenanceOrder
     */
    MaintenanceOrder getOrderById(Long orderId);

    /**
     * List all maintenance orders with pagination.
     * @param pageable pagination information
     * @return a page of MaintenanceOrders
     */
    Page<MaintenanceOrder> listOrders(Pageable pageable);

    /**
     * List maintenance orders for a specific aircraft with pagination.
     * @param aircraftId the ID of the aircraft
     * @param pageable pagination information
     * @return a page of MaintenanceOrders for the specified aircraft
     */
    Page<MaintenanceOrder> listOrdersByAircraft(Long aircraftId, Pageable pageable);

    /**
     * Change the status of a maintenance order.
     * @param orderId the ID of the order
     * @param newStatus the new status to set
     * @param completedBy the user completing the order (if applicable)
     * @return the updated MaintenanceOrder
     */
    MaintenanceOrder changeStatus(Long orderId, OrderStatus newStatus, User completedBy);
}