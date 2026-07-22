package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

@Component
public class IdleStrategy implements DroneStateStrategy {

    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.IDLE;
    }

    @Override
    public void execute(Drone drone) {
        // Boştaki İHA doğrudan göreve gönderilir.
        drone.setDurum(DroneStatus.IN_MISSION);
    }
}


