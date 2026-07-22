package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

@Component
public class ReturningStrategy implements DroneStateStrategy {

    private final double US_ENLEM = 39.92077;
    private final double US_BOYLAM = 32.85411;

    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.RETURNING;
    }

    @Override
    public void execute(Drone drone) {
        // 1. Üsse Doğru Yaklaşma
        if (drone.getEnlem() > US_ENLEM) drone.setEnlem(drone.getEnlem() - 0.05);
        if (drone.getEnlem() < US_ENLEM) drone.setEnlem(drone.getEnlem() + 0.05);
        if (drone.getBoylam() > US_BOYLAM) drone.setBoylam(drone.getBoylam() - 0.05);
        if (drone.getBoylam() < US_BOYLAM) drone.setBoylam(drone.getBoylam() + 0.05);

        drone.setBataryaSeviyesi(drone.getBataryaSeviyesi() - 2.5);

        // 2. Kritik Kontroller
        if (drone.getBataryaSeviyesi() <= 0) {
            drone.setDurum(DroneStatus.MAINTENANCE);
            drone.setBataryaSeviyesi(0);
        } else if (Math.abs(drone.getEnlem() - US_ENLEM) < 0.06 && Math.abs(drone.getBoylam() - US_BOYLAM) < 0.06) {
            drone.setEnlem(US_ENLEM);
            drone.setBoylam(US_BOYLAM);
            drone.setBataryaSeviyesi(100.0);
            drone.setDurum(DroneStatus.IDLE);
            System.out.println("BİLGİ: " + drone.getCagriKodu() + " üsse ulaştı. Batarya değiştirildi. Yeniden devreye alınıyor.");
        }
    }
}