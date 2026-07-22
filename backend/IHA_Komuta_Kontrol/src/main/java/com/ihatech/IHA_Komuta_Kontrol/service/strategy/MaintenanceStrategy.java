package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TACTICAL FLIGHT STRATEGY (MAINTENANCE / GROUNDED)
 * ============================================================================
 * Architecture : Strategy Pattern Implementation (@Component)
 * Purpose      : Handles UAV assets that are grounded for maintenance,
 *                repairs, or critical system failure. Enforces a no-op
 *                behavior, ensuring grounded assets do not execute telemetry
 *                updates or physical movement simulations.
 * ============================================================================
 */
@Component
public class MaintenanceStrategy implements DroneStateStrategy {

    /**
     * Evaluates whether this strategy handles the MAINTENANCE state.
     *
     * @param status The current operational state of the UAV
     * @return True if the status is MAINTENANCE, false otherwise
     */
    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.MAINTENANCE;
    }

    /**
     * Execution pipeline for grounded assets.
     * Intentionally left as a no-op since maintenance units remain static and inactive.
     *
     * @param drone The targeted Drone entity undergoing maintenance
     */
    @Override
    public void execute(Drone drone) {
        // Grounded assets do not participate in flight physics or telemetry updates.
    }
}