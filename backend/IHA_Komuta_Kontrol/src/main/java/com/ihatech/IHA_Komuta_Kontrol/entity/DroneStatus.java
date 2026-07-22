package com.ihatech.IHA_Komuta_Kontrol.entity;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - OPERATIONAL STATE MACHINE
 * ============================================================================
 * Architecture : Enumeration / State Pattern Context
 * Purpose      : Defines the absolute lifecycle states of a UAV.
 *                Used by the Autonomous Strategy Engine to determine the
 *                active flight, navigation, and telemetry algorithms.
 * ============================================================================
 */
public enum DroneStatus {

    /**
     * Standby Mode: UAV is grounded at the HQ, fully operational,
     * and awaiting deployment directives from the operator.
     */
    IDLE,

    /**
     * Active Engagement: UAV is airborne, executing its primary mission,
     * and autonomously navigating towards its designated tactical waypoints.
     */
    IN_MISSION,

    /**
     * Return To Base (RTB): Triggered manually by operator override or
     * autonomously via Bingo Fuel (critical battery) safety protocols.
     */
    RETURNING,

    /**
     * Offline/Grounded: UAV is undergoing maintenance, repairs, or has
     * critically failed. Completely disconnected from the active grid.
     */
    MAINTENANCE
}