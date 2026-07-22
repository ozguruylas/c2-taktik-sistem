package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TACTICAL FLIGHT STRATEGY (IN_MISSION)
 * ============================================================================
 * Architecture : Strategy Pattern Implementation (@Component)
 * Purpose      : Manages active airborne UAVs during mission execution.
 *                Calculates organic flight dynamics (turbulence & trajectory tracking),
 *                monitors power consumption, and triggers autonomous RTB (Bingo Fuel)
 *                or maintenance protocols when thresholds are breached.
 * ============================================================================
 */
@Component
public class InMissionStrategy implements DroneStateStrategy {

    // --- Flight Physics & Telemetry Constants (Magic Numbers Clean-up) ---
    private static final double BASE_SPEED = 0.02;
    private static final double TURBULENCE_FACTOR = 0.05;
    private static final double BATTERY_DRAIN_RATE = 2.5;
    private static final double BINGO_FUEL_THRESHOLD = 55.0;

    /**
     * Evaluates whether this strategy handles the IN_MISSION state.
     *
     * @param status The current operational state of the UAV
     * @return True if the status is IN_MISSION, false otherwise
     */
    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.IN_MISSION;
    }

    /**
     * Executes active flight physics, waypoint navigation, and power telemetry checks.
     *
     * @param drone The targeted Drone entity undergoing mission execution
     */
    @Override
    public void execute(Drone drone) {
        simulateFlightDynamics(drone);
        monitorTelemetry(drone);
    }

    /**
     * Simulates organic flight path dynamics including wind variance (turbulence)
     * and incremental waypoint progression towards target coordinates.
     */
    private void simulateFlightDynamics(Drone drone) {
        double hizX = BASE_SPEED + (Math.random() * TURBULENCE_FACTOR);
        double hizY = BASE_SPEED + (Math.random() * TURBULENCE_FACTOR);

        if (drone.getHedefEnlem() != null && drone.getHedefBoylam() != null) {
            // Progressive correction towards designated waypoint
            if (drone.getEnlem() < drone.getHedefEnlem()) drone.setEnlem(drone.getEnlem() + hizX);
            if (drone.getEnlem() > drone.getHedefEnlem()) drone.setEnlem(drone.getEnlem() - hizX);

            if (drone.getBoylam() < drone.getHedefBoylam()) drone.setBoylam(drone.getBoylam() + hizY);
            if (drone.getBoylam() > drone.getHedefBoylam()) drone.setBoylam(drone.getBoylam() - hizY);
        } else {
            // Unrestricted free-flight mode
            drone.setEnlem(drone.getEnlem() + hizX);
            drone.setBoylam(drone.getBoylam() + hizY);
        }
    }

    /**
     * Monitors power consumption and triggers safety overrides (Bingo Fuel RTB or Maintenance).
     */
    private void monitorTelemetry(Drone drone) {
        drone.setBataryaSeviyesi(drone.getBataryaSeviyesi() - BATTERY_DRAIN_RATE);

        if (drone.getBataryaSeviyesi() <= 0) {
            drone.setDurum(DroneStatus.MAINTENANCE);
            drone.setBataryaSeviyesi(0);
        } else if (drone.getBataryaSeviyesi() <= BINGO_FUEL_THRESHOLD) {
            drone.setDurum(DroneStatus.RETURNING);
            System.out.println("[BINGO FUEL ALERT] " + drone.getCagriKodu() + " returning to base!");
        }
    }
}