package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TACTICAL FLIGHT STRATEGY (RETURNING / RTB)
 * ============================================================================
 * Architecture : Strategy Pattern Implementation (@Component)
 * Purpose      : Manages UAV assets during the Return To Base (RTB) phase.
 *                Navigates the aircraft back to headquarters coordinates, monitors
 *                ongoing power consumption, and handles safe landing protocols
 *                (resetting battery and transitioning to IDLE status upon arrival).
 * ============================================================================
 */
@Component
public class ReturningStrategy implements DroneStateStrategy {

    // --- Headquarters Coordinates (Ankara HQ) ---
    private static final double HQ_LATITUDE = 39.92077;
    private static final double HQ_LONGITUDE = 32.85411;

    // --- Flight & Telemetry Constants (Magic Numbers Clean-up) ---
    private static final double RETURN_SPEED = 0.05;
    private static final double BATTERY_DRAIN_RATE = 2.5;
    private static final double ARRIVAL_THRESHOLD = 0.06;
    private static final double FULL_BATTERY = 100.0;

    /**
     * Evaluates whether this strategy handles the RETURNING state.
     *
     * @param status The current operational state of the UAV
     * @return True if the status is RETURNING, false otherwise
     */
    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.RETURNING;
    }

    /**
     * Executes the RTB navigation vector, power tracking, and arrival verification.
     *
     * @param drone The targeted Drone entity undergoing the return sequence
     */
    @Override
    public void execute(Drone drone) {
        navigateTowardsHQ(drone);
        checkLandingOrFailure(drone);
    }

    /**
     * Calculates the vector path guiding the UAV back to headquarters coordinates.
     */
    private void navigateTowardsHQ(Drone drone) {
        if (drone.getEnlem() > HQ_LATITUDE) drone.setEnlem(drone.getEnlem() - RETURN_SPEED);
        if (drone.getEnlem() < HQ_LATITUDE) drone.setEnlem(drone.getEnlem() + RETURN_SPEED);

        if (drone.getBoylam() > HQ_LONGITUDE) drone.setBoylam(drone.getBoylam() - RETURN_SPEED);
        if (drone.getBoylam() < HQ_LONGITUDE) drone.setBoylam(drone.getBoylam() + RETURN_SPEED);
    }

    /**
     * Monitors power depletion or verifies whether the UAV has successfully reached HQ.
     */
    private void checkLandingOrFailure(Drone drone) {
        drone.setBataryaSeviyesi(drone.getBataryaSeviyesi() - BATTERY_DRAIN_RATE);

        if (drone.getBataryaSeviyesi() <= 0) {
            drone.setDurum(DroneStatus.MAINTENANCE);
            drone.setBataryaSeviyesi(0);
        } else if (Math.abs(drone.getEnlem() - HQ_LATITUDE) < ARRIVAL_THRESHOLD &&
                Math.abs(drone.getBoylam() - HQ_LONGITUDE) < ARRIVAL_THRESHOLD) {

            drone.setEnlem(HQ_LATITUDE);
            drone.setBoylam(HQ_LONGITUDE);
            drone.setBataryaSeviyesi(FULL_BATTERY);
            drone.setDurum(DroneStatus.IDLE);

            System.out.println("[INFO] " + drone.getCagriKodu() + " successfully returned to base. Battery recharged. Restoring to standby.");
        }
    }
}