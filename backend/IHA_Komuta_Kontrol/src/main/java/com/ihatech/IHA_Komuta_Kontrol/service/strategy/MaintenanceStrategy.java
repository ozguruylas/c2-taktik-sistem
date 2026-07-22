package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceStrategy implements DroneStateStrategy {

    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.MAINTENANCE;
    }

    @Override
    public void execute(Drone drone) {
        // Bakımdaki / Düşmüş İHA hiçbir fiziksel tepki vermez, hareket etmez.
        // Bilinçli olarak boş bırakılmıştır.
    }
}