package tech.vanguardops.vanguardops.aircraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.vanguardops.vanguardops.aircraft.exception.AircraftDataConflictException;
import tech.vanguardops.vanguardops.aircraft.exception.AircraftNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AircraftServiceV1Tests {

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private AircraftServiceV1 aircraftServiceV1;

    private List<Aircraft> aircraftList;

    @BeforeEach
    public void setup() {
        // Setup code if needed
        aircraftList = List.of(
                new Aircraft(1L, "Boeing 737", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2)),
                new Aircraft(2L, "Airbus A320", "Commercial", "150", "Airbus", "A320neo", new BigDecimal(1650), 24000, LocalDateTime.now().minusDays(5), AircraftStatus.IN_MAINTENANCE, "Squadron-43", "California-1", LocalDateTime.now().minusDays(400), LocalDateTime.now().minusDays(5)),
                new Aircraft(3L, "Cessna 172", "Private", "4", "Cessna", "Skyhawk", new BigDecimal(300), 800, LocalDateTime.now().minusDays(1), AircraftStatus.AVAILABLE, "Squadron-44", "Florida-1", LocalDateTime.now().minusDays(100), LocalDateTime.now().minusDays(1))
        );
    }

    @DisplayName("Test get all aircrafts")
    @Test
    public void whenGetAllAircrafts_thenReturnAllAircrafts() {
        when(aircraftRepository.findAll())
                .thenReturn(aircraftList);

        List<Aircraft> result = aircraftServiceV1.getAll();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("Boeing 737", result.get(0).getTailNumber());
        Assertions.assertEquals("Airbus A320", result.get(1).getTailNumber());
        Assertions.assertEquals("Cessna 172", result.get(2).getTailNumber());
    }

    @DisplayName("Test get aircraft by invalid ID")
    @Test
    public void givenInvalidId_whenGetAircraftById_throwAircraftNotFoundException() {
        Long invalidId = 999L;
        when(aircraftRepository.findById(invalidId))
                .thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(AircraftNotFoundException.class, () -> aircraftServiceV1.getById(invalidId));
    }

    @DisplayName("Test get aircraft by valid ID")
    @Test
    public void givenValidId_whenGetAircraftById_thenReturnAircraft() {
        Long validId = 1L;
        Aircraft aircraft = aircraftList.getFirst();
        when(aircraftRepository.findById(validId))
                .thenReturn(java.util.Optional.of(aircraft));

        Aircraft result = aircraftServiceV1.getById(validId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(validId, result.getId());
        Assertions.assertEquals("Boeing 737", result.getTailNumber());
    }

    @DisplayName("Test create aircraft with duplicate tail number")
    @Test
    public void givenDuplicateTailNumber_whenCreateAircraft_throwAircraftDataConflictException() {
        // Arrange
        Aircraft newAircraft = new Aircraft(null, "Boeing 737", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        when(aircraftRepository.existsByTailNumberIgnoreCase("Boeing 737"))
                .thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(AircraftDataConflictException.class, () -> aircraftServiceV1.create(newAircraft));
    }

    @DisplayName("Test create aircraft with duplicate serial number")
    @Test
    public void givenDuplicateSerialNumber_whenCreateAircraft_throwAircraftDataConflictException() {
        // Arrange
        Aircraft newAircraft = new Aircraft(null, "New Tail", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        when(aircraftRepository.existsByTailNumberIgnoreCase("New Tail"))
                .thenReturn(false);
        when(aircraftRepository.existsBySerialNumberIgnoreCase("737 MAX"))
                .thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(AircraftDataConflictException.class, () -> aircraftServiceV1.create(newAircraft));
    }

    @DisplayName("Test create aircraft successfully")
    @Test
    public void givenValidAircraft_whenCreateAircraft_thenReturnCreatedAircraft() {
        // Arrange
        Aircraft newAircraft = new Aircraft(null, "New Tail", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        Aircraft savedAircraft = new Aircraft(4L, "New Tail", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        when(aircraftRepository.existsByTailNumberIgnoreCase("New Tail"))
                .thenReturn(false);
        when(aircraftRepository.existsBySerialNumberIgnoreCase("737 MAX"))
                .thenReturn(false);
        when(aircraftRepository.save(newAircraft))
                .thenReturn(savedAircraft);

        // Act
        Aircraft result = aircraftServiceV1.create(newAircraft);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(4L, result.getId());
        Assertions.assertEquals("New Tail", result.getTailNumber());
    }

    @DisplayName("Test update aircraft with duplicate tail number")
    @Test
    public void givenDuplicateTailNumber_whenUpdateAircraft_throwAircraftDataConflictException() {
        // Arrange
        Long aircraftId = 1L;
        Aircraft updatedAircraft = new Aircraft(null, "Airbus A320", "Commercial", "160", "Boeing", "737 MAX", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        when(aircraftRepository.existsByTailNumberIgnoreCaseAndIdNot("Airbus A320", 1L))
                .thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(AircraftDataConflictException.class, () -> aircraftServiceV1.update(aircraftId, updatedAircraft));
    }

    @DisplayName("Test update aircraft with duplicate serial number")
    @Test
    public void givenDuplicateSerialNumber_whenUpdateAircraft_throwAircraftDataConflictException() {
        // Arrange
        Long aircraftId = 1L;
        Aircraft updatedAircraft = new Aircraft(null, "New Tail", "Commercial", "160", "Boeing", "A320neo", new BigDecimal(1703), 23000, LocalDateTime.now().minusDays(2), AircraftStatus.AVAILABLE, "Squadron-42", "Texas-1", LocalDateTime.now().minusDays(324), LocalDateTime.now().minusDays(2));

        when(aircraftRepository.existsByTailNumberIgnoreCaseAndIdNot("New Tail", 1L))
                .thenReturn(false);
        when(aircraftRepository.existsBySerialNumberIgnoreCaseAndIdNot("A320neo", 1L))
                .thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(AircraftDataConflictException.class, () -> aircraftServiceV1.update(aircraftId, updatedAircraft));
    }

    @DisplayName("Test delete with invalid ID")
    @Test
    public void givenInvalidID_whenDeleteAircraft_throwAircraftNotFoundException() {
        Long invalidId = 999L;
        when(aircraftRepository.existsById(invalidId))
                .thenReturn(false);

        Assertions.assertThrows(AircraftNotFoundException.class, () -> aircraftServiceV1.delete(invalidId));
    }

    @DisplayName("Test delete with valid ID")
    @Test
    public void givenValidID_whenDeleteAircraft_thenDeleteSuccessfully() {
        Long validId = 1L;
        when(aircraftRepository.existsById(validId))
                .thenReturn(true);

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> aircraftServiceV1.delete(validId));

        // Verify deletion
        // Check that the repository's deleteById method was called
        verify(aircraftRepository).deleteById(validId);
    }
}