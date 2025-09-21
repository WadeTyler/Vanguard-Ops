package tech.vanguardops.vanguardops.maintenance;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.maintenance.dto.MaintenanceOrderDTO;
import tech.vanguardops.vanguardops.maintenance.dto.ManageOrderRequest;

/**
 * REST controller for managing maintenance orders.
 */
@RestController
@RequestMapping("/api/v1/maintenance-orders")
public class MaintenanceOrderController {

    private final MaintenanceService maintenanceService;
    private final MaintenaceOrderMapper orderMapper;

    public MaintenanceOrderController(@Qualifier("maintenanceServiceV1") MaintenanceService maintenanceService, MaintenaceOrderMapper orderMapper) {
        this.maintenanceService = maintenanceService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER')")
    public MaintenanceOrderDTO placeOrder(@RequestBody @Valid ManageOrderRequest manageOrderRequest,
                                          @AuthenticationPrincipal User user) {
        MaintenanceOrder order = maintenanceService.placeOrder(manageOrderRequest, user);
        return orderMapper.toDTO(order);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER')")
    public MaintenanceOrderDTO updateOrder(@RequestBody @Valid ManageOrderRequest manageOrderRequest,
                                             @PathVariable Long orderId) {
        MaintenanceOrder order = maintenanceService.updateOrder(manageOrderRequest, orderId);
        return orderMapper.toDTO(order);
    }

    @GetMapping
    public Page<MaintenanceOrderDTO> listOrders(Pageable pageable) {
        return maintenanceService.listOrders(pageable).map(orderMapper::toDTO);
    }

    @GetMapping("/aircraft/{aircraftId}")
    public Page<MaintenanceOrderDTO> listOrdersByAircraft(@PathVariable Long aircraftId, Pageable pageable) {
        return maintenanceService.listOrdersByAircraft(aircraftId, pageable).map(orderMapper::toDTO);
    }

    @GetMapping("/{orderId}")
    public MaintenanceOrderDTO getOrderById(@PathVariable Long orderId) {
        MaintenanceOrder order = maintenanceService.getOrderById(orderId);
        return orderMapper.toDTO(order);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLANNER', 'TECHNICIAN')")
    public MaintenanceOrderDTO changeOrderStatus(@PathVariable Long orderId,
                                                 @RequestParam OrderStatus newStatus,
                                                 @AuthenticationPrincipal User user) {
        MaintenanceOrder order = maintenanceService.changeStatus(orderId, newStatus, user);
        return orderMapper.toDTO(order);
    }




}