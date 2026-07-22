package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TACTICAL FLIGHT STRATEGY (IDLE / STANDBY DEPLOYMENT)
 * ============================================================================
 * Architecture : Strategy Pattern Implementation (@Component)
 * Purpose      : Manages UAV assets currently in standby mode at the base HQ.
 *                Automatically transitions combat-ready idle assets into active
 *                engagement missions when a deployment directive is triggered.
 * ============================================================================
 */
@Component
public class IdleStrategy implements DroneStateStrategy {

    /**
     * Evaluates whether this strategy handles the IDLE state.
     *
     * @param status The current operational state of the UAV
     * @return True if the status is IDLE, false otherwise
     */
    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.IDLE;
    }

    /**
     * Executes the deployment protocol for a grounded asset.
     * Shifts the operational state from standby to active mission engagement.
     *
     * @param drone The targeted Drone entity undergoing deployment
     */
    @Override
    public void execute(Drone drone) {
        drone.setDurum(DroneStatus.IN_MISSION);
    }
}