package tech.vanguardops.vanguardops.maintenanceorder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.vanguardops.vanguardops.aircraft.Aircraft;
import tech.vanguardops.vanguardops.aircraft.AircraftService;
import tech.vanguardops.vanguardops.aircraft.AircraftStatus;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.maintenanceorder.dto.ManageOrderRequest;
import tech.vanguardops.vanguardops.maintenanceorder.exception.ExistingOrderException;
import tech.vanguardops.vanguardops.maintenanceorder.exception.MaintenaceOrderStatusChangeException;
import tech.vanguardops.vanguardops.maintenanceorder.exception.MaintenanceOrderNotFoundException;

import java.time.LocalDateTime;

/**
 * Service implementation for managing maintenance orders.
 */
@Service
public class MaintenanceServiceV1 implements MaintenanceService {
    private final MaintenanceOrderRepository orderRepository;
    private final AircraftService aircraftService;

    public MaintenanceServiceV1(MaintenanceOrderRepository orderRepository,
                                @Qualifier("aircraftServiceV1") AircraftService aircraftService) {
        this.orderRepository = orderRepository;
        this.aircraftService = aircraftService;
    }

    @Override
    @Transactional
    public MaintenanceOrder placeOrder(ManageOrderRequest request, User placedBy) {
        // Find existing aircraft
        Aircraft aircraft = aircraftService.getById(request.aircraftId());

        // If an OPEN order already exists for this aircraft
        if (orderRepository.existsByAircraftAndStatus(aircraft, OrderStatus.OPEN)) {
            throw new ExistingOrderException("An open maintenance order already exists for this aircraft.");
        }

        // Create and save new order
        MaintenanceOrder newOrder = MaintenanceOrder.builder()
                .aircraft(aircraft)
                .priority(request.priority())
                .status(OrderStatus.OPEN)
                .description(request.description())
                .placedBy(placedBy)
                .completedBy(null)
                .completedAt(null)
                .build();

        return orderRepository.save(newOrder);
    }

    @Override
    @Transactional
    public MaintenanceOrder updateOrder(ManageOrderRequest request, Long orderId) {
        // Find existing order
        MaintenanceOrder order = getOrderById(orderId);

        // Update order details
        // Note: Status and completion details are not updated here and are handled separately
        order.setPriority(request.priority());
        order.setDescription(request.description());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public MaintenanceOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new MaintenanceOrderNotFoundException("Maintenance order not found with id: " + orderId));
    }

    @Override
    @Transactional
    public Page<MaintenanceOrder> listOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Page<MaintenanceOrder> listOrdersByAircraft(Long aircraftId, Pageable pageable) {
        Aircraft aircraft = aircraftService.getById(aircraftId);
        return orderRepository.findAllByAircraft(aircraft, pageable);
    }

    @Override
    @Transactional
    public MaintenanceOrder changeStatus(Long orderId, OrderStatus newStatus, User authUser) {
        MaintenanceOrder order = getOrderById(orderId);

        switch (newStatus) {
            case IN_PROGRESS -> {
                return startOrder(order);
            }
            case CANCELLED -> {
                return cancelOrder(order);
            }
            case OPEN -> {
                return reopenOrder(order);
            }
            case COMPLETED -> {
                return completeOrder(order, authUser);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported status: " + newStatus + ". Valid statuses are: OPEN, IN_PROGRESS, COMPLETED, CANCELLED.");
        }
    }

    /**
     * Start a maintenance order.
     * @param order the order to start
     * @return the updated order
     */
    private MaintenanceOrder startOrder(MaintenanceOrder order) {
        // Only OPEN orders can be started
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new MaintenaceOrderStatusChangeException("Only OPEN orders can be started.");
        }

        // Update status to IN_PROGRESS
        order.setStatus(OrderStatus.IN_PROGRESS);
        var updatedOrder = orderRepository.save(order);

        // Put the aircraft into maintenance mode
        aircraftService.setStatus(order.getAircraft().getId(), AircraftStatus.IN_MAINTENANCE);
        return updatedOrder;
    }

    /**
     * Cancel a maintenance order.
     * @param order the order to cancel
     * @return the updated order
     */
    private MaintenanceOrder cancelOrder(MaintenanceOrder order) {
        // Only OPEN or IN_PROGRESS orders can be cancelled
        if (order.getStatus() != OrderStatus.OPEN && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new MaintenaceOrderStatusChangeException("Only OPEN or IN_PROGRESS orders can be cancelled.");
        }

        // If the order was IN_PROGRESS, set the aircraft back to AVAILABLE
        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            aircraftService.setStatus(order.getAircraft().getId(), AircraftStatus.AVAILABLE);
        }

        // Update status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Reopen a maintenance order.
     * @param order the order to reopen
     * @return the updated order
     */
    private MaintenanceOrder reopenOrder(MaintenanceOrder order) {
        // Only non-open orders can be reopened
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new MaintenaceOrderStatusChangeException("Order must be in progress to be reopened.");
        }

            aircraftService.setStatus(order.getAircraft().getId(), AircraftStatus.AVAILABLE);

        // Update status to OPEN
        order.setStatus(OrderStatus.OPEN);

        return orderRepository.save(order);
    }

    /**
     * Complete a maintenance order.
     * @param order the order to complete
     * @param completedBy the user completing the order
     * @return the updated order
     */
    private MaintenanceOrder completeOrder(MaintenanceOrder order, User completedBy) {
        // Only IN_PROGRESS orders can be completed
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new MaintenaceOrderStatusChangeException("Only IN_PROGRESS orders can be completed.");
        }

        // Update status to COMPLETED and set completion details
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedBy(completedBy);
        order.setCompletedAt(LocalDateTime.now());
        var updatedOrder = orderRepository.save(order);

        // Set the aircraft back to AVAILABLE
        aircraftService.setStatus(order.getAircraft().getId(), AircraftStatus.AVAILABLE);
        return updatedOrder;
    }


}