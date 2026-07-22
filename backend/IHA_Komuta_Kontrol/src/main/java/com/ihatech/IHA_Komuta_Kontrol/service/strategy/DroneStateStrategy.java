package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - BEHAVIORAL STRATEGY CONTRACT
 * ============================================================================
 * Architecture : Strategy Pattern Interface
 * Purpose      : Defines the blueprint for autonomous UAV state behaviors.
 *                Enables the Open/Closed Principle (OCP), allowing new flight
 *                and management logics to be injected without altering core services.
 * ============================================================================
 */
public interface DroneStateStrategy {

    /**
     * Evaluates whether this strategy implementation supports the given operational state.
     *
     * @param status The current operational state of the UAV (e.g., IN_MISSION)
     * @return True if this strategy handles the given state, false otherwise
     */
    boolean supports(DroneStatus status);

    /**
     * Executes the core business logic, physics, and telemetry updates
     * for the UAV matching the active state condition.
     *
     * @param drone The targeted Drone entity undergoing state execution
     */
    void execute(Drone drone);
}