package com.ihatech.IHA_Komuta_Kontrol.repository;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - DATA ACCESS LAYER
 * ============================================================================
 * Architecture : Repository Pattern / Spring Data JPA
 * Purpose      : Abstracts database interactions and provides query derivation
 *                mechanisms for UAV fleet management and telemetry persistence.
 * ============================================================================
 */
@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    /**
     * Retrieves a subset of the fleet based on their current operational status.
     *
     * @param durum The target operational state (e.g., IDLE, IN_MISSION)
     * @return List of UAVs matching the specified status
     */
    List<Drone> findByDurum(DroneStatus durum);

    /**
     * Mission Allocation Intelligence: Locates UAVs that are both available
     * and have sufficient power reserves to undertake a new tactical directive.
     *
     * @param durum The required state (typically IDLE)
     * @param bataryaSeviyesi The minimum acceptable battery threshold for deployment
     * @return List of combat-ready UAVs matching the criteria
     */
    List<Drone> findByDurumAndBataryaSeviyesiGreaterThan(DroneStatus durum, double bataryaSeviyesi);

    /**
     * Precision Lookup: Fetches a specific UAV using its unique tactical call sign.
     *
     * @param cagriKodu The exact call sign of the UAV (e.g., "AKINCI-1")
     * @return The specific Drone entity, or null if no match is found
     */
    Drone findByCagriKodu(String cagriKodu);
}