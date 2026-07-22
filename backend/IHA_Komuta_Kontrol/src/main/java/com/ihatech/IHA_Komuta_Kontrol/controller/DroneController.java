package com.ihatech.IHA_Komuta_Kontrol.controller;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import com.ihatech.IHA_Komuta_Kontrol.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - REST API GATEWAY
 * ============================================================================
 * Architecture : RESTful API Controller
 * Purpose      : Acts as the primary communication interface between the
 *                Frontend Operator UI and the Backend Autonomous Engine.
 * ============================================================================
 */
@RestController
@RequestMapping("/api/drones")
@CrossOrigin(origins = "*") // Allows cross-origin requests from the operator dashboard
@RequiredArgsConstructor
public class DroneController {

    private final DroneService droneService;

    /**
     * Deployment Protocol: Registers a new UAV into the operational grid.
     * Deserializes the incoming payload and delegates persistence to the service layer.
     *
     * @param drone The UAV data payload from the operator panel
     * @return The registered Drone entity with its assigned database ID
     */
    @PostMapping("/ekle")
    public ResponseEntity<Drone> sistemeIhaEkle(@RequestBody Drone drone) {
        Drone kaydedilenDrone = droneService.sistemeIhaEkle(drone);
        return ResponseEntity.ok(kaydedilenDrone);
    }

    /**
     * Telemetry Synchronization: Retrieves the complete status snapshot of the fleet.
     * Used primarily for the initial data hydration of the C2 UI before
     * WebSocket streaming takes over.
     *
     * @return List of all active and inactive UAVs in the database
     */
    @GetMapping("/filo")
    public ResponseEntity<List<Drone>> tumFiloyuGetir() {
        return ResponseEntity.ok(droneService.tumFiloyuGetir());
    }

    /**
     * Performs a precise lookup for a specific UAV using its unique call sign.
     *
     * @param cagriKodu The unique identifier (e.g., "AKINCI-1")
     * @return The corresponding Drone entity
     */
    @GetMapping("/bul/{cagriKodu}")
    public ResponseEntity<Drone> cagriKoduIleBul(@PathVariable String cagriKodu) {
        return ResponseEntity.ok(droneService.cagriKoduIleBul(cagriKodu));
    }

    /**
     * Mission Allocation: Identifies and returns the most suitable UAV
     * for a new mission based on battery levels and current availability.
     *
     * @return The optimal Drone entity ready for deployment
     */
    @GetMapping("/gorev-icin-uygun")
    public ResponseEntity<Drone> gorevIcinUygunIhaBul() {
        return ResponseEntity.ok(droneService.gorevIcinUygunIhaBul());
    }

    /**
     * OPERATOR OVERRIDE: Forces a specific UAV to abort its current mission
     * and immediately initiate the Return To Base (RTB) sequence.
     *
     * @param id The database ID of the target UAV
     * @return The updated Drone entity with RETURNING status
     */
    @PostMapping("/{id}/rtb")
    public ResponseEntity<Drone> acilRtbEmri(@PathVariable Long id) {
        Drone guncellenenDrone = droneService.durumGuncelle(id, DroneStatus.RETURNING);
        return ResponseEntity.ok(guncellenenDrone);
    }
}