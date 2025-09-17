package tech.vanguardops.vanguardops.aircraft;

import java.util.List;

public interface AircraftService {

    /**
     * Create a new aircraft
     * @param aircraft the aircraft to create
     * @return the created aircraft
     */
    Aircraft create(Aircraft aircraft);

    /**
     * Update an existing aircraft
     * @param id the id of the aircraft to update
     * @param aircraft the aircraft data to update
     * @return the updated aircraft
     */
    Aircraft update(Long id, Aircraft aircraft);

    /**
     * Get all aircraft
     * @return the list of all aircraft
     */
    List<Aircraft> getAll();

    /**
     * Get an aircraft by id
     * @param id the id of the aircraft to get
     * @return the aircraft
     */
    Aircraft getById(Long id);

    /**
     * Delete an aircraft by id
     * @param id the id of the aircraft to delete
     */
    void delete(Long id);
}