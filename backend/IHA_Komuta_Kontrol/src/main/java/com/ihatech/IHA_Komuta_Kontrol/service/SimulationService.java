package com.ihatech.IHA_Komuta_Kontrol.service;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.repository.DroneRepository;
import com.ihatech.IHA_Komuta_Kontrol.service.strategy.DroneStateStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - SIMULATION & TELEMETRY ENGINE
 * ============================================================================
 * Architecture : Event-Driven / Scheduled Task / Strategy Pattern Context
 * Purpose      : Acts as the heartbeat of the C2 backend. Periodically
 *                triggers the autonomous decision-making algorithms and
 *                broadcasts live telemetry data to the connected operator UI.
 * ============================================================================
 */
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final DroneRepository droneRepository;
    private final List<DroneStateStrategy> stateStrategies;
    private final SimpMessagingTemplate messagingTemplate;

    // --- Communication Frequencies ---
    private static final String RADAR_TOPIC = "/topic/drones";

    /**
     * Autonomous Lifecycle Loop (Heartbeat).
     * Executes dynamically at a fixed rate of 3000 milliseconds (3 seconds).
     *
     * Phase 1: Fetches the current operational snapshot of the entire fleet.
     * Phase 2: Delegates flight physics and mission logic to the appropriate Strategy.
     * Phase 3: Persists the calculated state to the database.
     * Phase 4: Broadcasts the synchronized telemetry via WebSocket/STOMP.
     */
    @Scheduled(fixedRate = 3000)
    public void ucaklariHareketEttir() {
        List<Drone> dronelar = droneRepository.findAll();

        // Execute Autonomous Logic Engine
        for (Drone drone : dronelar) {

            // Dynamic State Delegation (Strategy Pattern)
            for (DroneStateStrategy strategy : stateStrategies) {
                if (strategy.supports(drone.getDurum())) {
                    strategy.execute(drone);
                    break;
                }
            }

            // Persist the updated state physics
            droneRepository.save(drone);
        }

        // Real-Time Telemetry Broadcast to the Operator Grid
        messagingTemplate.convertAndSend(RADAR_TOPIC, dronelar);
    }
}