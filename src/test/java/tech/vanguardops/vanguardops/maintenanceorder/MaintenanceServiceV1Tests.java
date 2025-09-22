package tech.vanguardops.vanguardops.maintenanceorder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import tech.vanguardops.vanguardops.aircraft.Aircraft;
import tech.vanguardops.vanguardops.aircraft.AircraftService;
import tech.vanguardops.vanguardops.aircraft.AircraftStatus;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.maintenanceorder.dto.ManageOrderRequest;
import tech.vanguardops.vanguardops.maintenanceorder.exception.ExistingOrderException;
import tech.vanguardops.vanguardops.maintenanceorder.exception.MaintenaceOrderStatusChangeException;
import tech.vanguardops.vanguardops.maintenanceorder.exception.MaintenanceOrderNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceServiceV1Tests {

    @Mock
    private MaintenanceOrderRepository orderRepository;

    @Mock
    private AircraftService aircraftService;

    @InjectMocks
    private MaintenanceServiceV1 maintenanceService;

    private Aircraft aircraft;
    private User user;

    @BeforeEach
    void setup() {
        aircraft = Aircraft.builder()
                .id(1L)
                .tailNumber("TAIL-001")
                .model("Model-X")
                .manufacturer("Maker Co.")
                .serialNumber("SN-123")
                .status(AircraftStatus.AVAILABLE)
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("tech")
                .passwordHash("hash")
                .firstName("Tech")
                .lastName("User")
                .build();
    }

    @DisplayName("placeOrder: throws when an OPEN order already exists for aircraft")
    @Test
    void givenExistingOpenOrder_whenPlaceOrder_thenThrowExistingOrderException() {
        ManageOrderRequest req = new ManageOrderRequest(aircraft.getId(), "Hydraulic leak", 2);
        when(aircraftService.getById(aircraft.getId())).thenReturn(aircraft);
        when(orderRepository.existsByAircraftAndStatus(aircraft, OrderStatus.OPEN)).thenReturn(true);

        assertThrows(ExistingOrderException.class, () -> maintenanceService.placeOrder(req, user));

        verify(aircraftService).getById(aircraft.getId());
        verify(orderRepository, never()).save(any());
    }

    @DisplayName("placeOrder: creates and saves a new OPEN order")
    @Test
    void givenValidRequest_whenPlaceOrder_thenSaveAndReturnOrder() {
        ManageOrderRequest req = new ManageOrderRequest(aircraft.getId(), "Oil change", 1);
        when(aircraftService.getById(aircraft.getId())).thenReturn(aircraft);
        when(orderRepository.existsByAircraftAndStatus(aircraft, OrderStatus.OPEN)).thenReturn(false);

        ArgumentCaptor<MaintenanceOrder> captor = ArgumentCaptor.forClass(MaintenanceOrder.class);
        MaintenanceOrder saved = MaintenanceOrder.builder()
                .id(10L)
                .aircraft(aircraft)
                .description("Oil change")
                .priority(1)
                .status(OrderStatus.OPEN)
                .placedBy(user)
                .build();
        when(orderRepository.save(any(MaintenanceOrder.class))).thenReturn(saved);

        MaintenanceOrder result = maintenanceService.placeOrder(req, user);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(OrderStatus.OPEN, result.getStatus());
        assertEquals("Oil change", result.getDescription());
        assertEquals(1, result.getPriority());
        assertEquals(user, result.getPlacedBy());
        assertNull(result.getCompletedBy());
        assertNull(result.getCompletedAt());

        verify(orderRepository).save(captor.capture());
        MaintenanceOrder toSave = captor.getValue();
        assertEquals(OrderStatus.OPEN, toSave.getStatus());
        assertEquals(aircraft, toSave.getAircraft());
        assertEquals("Oil change", toSave.getDescription());
        assertEquals(1, toSave.getPriority());
    }

    @DisplayName("getOrderById: throws when not found")
    @Test
    void givenInvalidId_whenGetOrderById_thenThrowNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(MaintenanceOrderNotFoundException.class, () -> maintenanceService.getOrderById(999L));
    }

    @DisplayName("getOrderById: returns when found")
    @Test
    void givenValidId_whenGetOrderById_thenReturnOrder() {
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(5L)
                .aircraft(aircraft)
                .status(OrderStatus.OPEN)
                .priority(3)
                .description("Check avionics")
                .build();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        MaintenanceOrder result = maintenanceService.getOrderById(5L);
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(OrderStatus.OPEN, result.getStatus());
    }

    @DisplayName("updateOrder: updates description and priority only")
    @Test
    void givenExistingOrder_whenUpdateOrder_thenFieldsUpdated() {
        Long id = 7L;
        MaintenanceOrder existing = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.OPEN)
                .priority(5)
                .description("Old")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        ManageOrderRequest updateReq = new ManageOrderRequest(aircraft.getId(), "New desc", 2);
        MaintenanceOrder updated = maintenanceService.updateOrder(updateReq, id);

        assertEquals("New desc", updated.getDescription());
        assertEquals(2, updated.getPriority());
        assertEquals(OrderStatus.OPEN, updated.getStatus());
    }

    @DisplayName("listOrders: returns paginated results")
    @Test
    void whenListOrders_thenReturnPage() {
        var pageReq = PageRequest.of(0, 10);
        var list = List.of(
                MaintenanceOrder.builder().id(1L).aircraft(aircraft).status(OrderStatus.OPEN).priority(1).description("A").build(),
                MaintenanceOrder.builder().id(2L).aircraft(aircraft).status(OrderStatus.CANCELLED).priority(2).description("B").build()
        );
        when(orderRepository.findAll(pageReq)).thenReturn(new PageImpl<>(list));
        Page<MaintenanceOrder> page = maintenanceService.listOrders(pageReq);
        assertEquals(2, page.getTotalElements());
    }

    @DisplayName("listOrdersByAircraft: fetches aircraft then returns paginated results")
    @Test
    void whenListOrdersByAircraft_thenReturnPage() {
        var pageReq = PageRequest.of(0, 5);
        when(aircraftService.getById(aircraft.getId())).thenReturn(aircraft);
        var list = List.of(
                MaintenanceOrder.builder().id(3L).aircraft(aircraft).status(OrderStatus.OPEN).priority(1).description("C").build()
        );
        when(orderRepository.findAllByAircraft(aircraft, pageReq)).thenReturn(new PageImpl<>(list));

        Page<MaintenanceOrder> page = maintenanceService.listOrdersByAircraft(aircraft.getId(), pageReq);
        assertEquals(1, page.getTotalElements());
        verify(aircraftService).getById(aircraft.getId());
    }

    @DisplayName("changeStatus: OPEN -> IN_PROGRESS starts order and sets aircraft IN_MAINTENANCE")
    @Test
    void givenOpenOrder_whenChangeStatusToInProgress_thenStarted() {
        Long id = 11L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.OPEN)
                .priority(1)
                .description("Start it")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceOrder updated = maintenanceService.changeStatus(id, OrderStatus.IN_PROGRESS, user);

        assertEquals(OrderStatus.IN_PROGRESS, updated.getStatus());
        verify(aircraftService).setStatus(aircraft.getId(), AircraftStatus.IN_MAINTENANCE);
    }

    @DisplayName("changeStatus: non-OPEN -> IN_PROGRESS throws")
    @Test
    void givenNonOpenOrder_whenChangeStatusToInProgress_thenThrow() {
        Long id = 12L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.CANCELLED)
                .priority(1)
                .description("Invalid start")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(MaintenaceOrderStatusChangeException.class, () ->
                maintenanceService.changeStatus(id, OrderStatus.IN_PROGRESS, user));

        verify(aircraftService, never()).setStatus(anyLong(), any());
    }

    @DisplayName("changeStatus: IN_PROGRESS -> CANCELLED sets aircraft AVAILABLE")
    @Test
    void givenInProgressOrder_whenChangeStatusToCancelled_thenAircraftAvailable() {
        Long id = 13L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.IN_PROGRESS)
                .priority(1)
                .description("Cancel it")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceOrder updated = maintenanceService.changeStatus(id, OrderStatus.CANCELLED, user);

        assertEquals(OrderStatus.CANCELLED, updated.getStatus());
        verify(aircraftService).setStatus(aircraft.getId(), AircraftStatus.AVAILABLE);
    }

    @DisplayName("changeStatus: OPEN -> CANCELLED does not change aircraft status")
    @Test
    void givenOpenOrder_whenChangeStatusToCancelled_thenNoAircraftChange() {
        Long id = 14L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.OPEN)
                .priority(1)
                .description("Cancel directly")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceOrder updated = maintenanceService.changeStatus(id, OrderStatus.CANCELLED, user);

        assertEquals(OrderStatus.CANCELLED, updated.getStatus());
        verify(aircraftService, never()).setStatus(anyLong(), eq(AircraftStatus.AVAILABLE));
    }

    @DisplayName("changeStatus: IN_PROGRESS -> OPEN reopens and sets aircraft AVAILABLE")
    @Test
    void givenInProgressOrder_whenChangeStatusToOpen_thenReopened() {
        Long id = 15L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.IN_PROGRESS)
                .priority(1)
                .description("Reopen")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceOrder updated = maintenanceService.changeStatus(id, OrderStatus.OPEN, user);

        assertEquals(OrderStatus.OPEN, updated.getStatus());
        verify(aircraftService).setStatus(aircraft.getId(), AircraftStatus.AVAILABLE);
    }

    @DisplayName("changeStatus: IN_PROGRESS -> COMPLETED sets completion fields and aircraft AVAILABLE")
    @Test
    void givenInProgressOrder_whenChangeStatusToCompleted_thenCompleted() {
        Long id = 16L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.IN_PROGRESS)
                .priority(1)
                .description("Finish")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MaintenanceOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceOrder updated = maintenanceService.changeStatus(id, OrderStatus.COMPLETED, user);

        assertEquals(OrderStatus.COMPLETED, updated.getStatus());
        assertEquals(user, updated.getCompletedBy());
        assertNotNull(updated.getCompletedAt());
        verify(aircraftService).setStatus(aircraft.getId(), AircraftStatus.AVAILABLE);
    }

    @DisplayName("changeStatus: non-IN_PROGRESS -> COMPLETED throws")
    @Test
    void givenNonInProgressOrder_whenChangeStatusToCompleted_thenThrow() {
        Long id = 17L;
        MaintenanceOrder order = MaintenanceOrder.builder()
                .id(id)
                .aircraft(aircraft)
                .status(OrderStatus.OPEN)
                .priority(1)
                .description("Invalid complete")
                .build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(MaintenaceOrderStatusChangeException.class, () ->
                maintenanceService.changeStatus(id, OrderStatus.COMPLETED, user));

        verify(aircraftService, never()).setStatus(anyLong(), any());
    }
}