package com.ihatech.IHA_Komuta_Kontrol.service;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import com.ihatech.IHA_Komuta_Kontrol.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - CORE BUSINESS LOGIC
 * ============================================================================
 * Architecture : Service Layer / Facade
 * Purpose      : Manages the lifecycle, deployment rules, and tactical state
 *                transitions of the UAV fleet. Acts as the primary bridge
 *                between API endpoints and database persistence.
 * ============================================================================
 */
@Service
@RequiredArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;

    // --- System Constraints & Tactical Thresholds ---
    private static final double MIN_MISSION_BATTERY_THRESHOLD = 20.0;

    /**
     * Fleet Registration Protocol: Initializes a new UAV into the grid.
     * Enforces the operational rule that all new assets must start in standby mode.
     *
     * @param drone The raw UAV data payload
     * @return The persisted Drone entity with generated database ID
     */
    public Drone sistemeIhaEkle(Drone drone) {
        drone.setDurum(DroneStatus.IDLE);
        return droneRepository.save(drone);
    }

    /**
     * Telemetry Aggregation: Retrieves the global state of all UAV assets.
     *
     * @return Comprehensive list of the entire fleet
     */
    public List<Drone> tumFiloyuGetir() {
        return droneRepository.findAll();
    }

    /**
     * Asset Identification: Performs a targeted lookup using a call sign.
     *
     * @param cagriKodu The tactical call sign (e.g., "AKINCI-1")
     * @return The specific Drone entity, or null if not found
     */
    public Drone cagriKoduIleBul(String cagriKodu) {
        return droneRepository.findByCagriKodu(cagriKodu);
    }

    /**
     * Tactical Deployment Intelligence: Scans the fleet to identify the most
     * optimal UAV for an immediate mission based on readiness and power reserves.
     *
     * @return The selected Drone entity ready for launch
     * @throws RuntimeException If no capable UAV is found in the grid
     */
    public Drone gorevIcinUygunIhaBul() {
        List<Drone> uygunIhalar = droneRepository.findByDurumAndBataryaSeviyesiGreaterThan(
                DroneStatus.IDLE,
                MIN_MISSION_BATTERY_THRESHOLD
        );

        if (uygunIhalar.isEmpty()) {
            throw new RuntimeException("Sistem Uyarisi: Gorev icin uygun, sarji yeterli veya bosta olan IHA bulunamadi!");
        }

        // Note: Currently selects the first available asset.
        // Future implementation can include geospatial proximity sorting.
        return uygunIhalar.get(0);
    }

    /**
     * State Transition Override: Updates the operational status of a UAV.
     * Used for manual operator commands (e.g., RTB) or autonomous algorithms.
     *
     * @param id The database identity of the UAV
     * @param yeniDurum The target operational state to be applied
     * @return The updated and persisted Drone entity
     * @throws RuntimeException If the specified identity does not exist
     */
    public Drone durumGuncelle(Long id, DroneStatus yeniDurum) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kritik Hata: Verilen ID'ye sahip IHA veritabaninda bulunamadi!"));

        drone.setDurum(yeniDurum);
        return droneRepository.save(drone);
    }
}