package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.stereotype.Component;

@Component
public class InMissionStrategy implements DroneStateStrategy {

    @Override
    public boolean supports(DroneStatus status) {
        return status == DroneStatus.IN_MISSION;
    }

    @Override
    public void execute(Drone drone) {
        // YENİ: Organik Uçuş (Manevra ve Türbülans Simülasyonu)
        // Hız her saniye 0.02 ile 0.07 arasında değişkenlik gösterecek
        double hizX = 0.02 + (Math.random() * 0.05);
        double hizY = 0.02 + (Math.random() * 0.05);

        if (drone.getHedefEnlem() != null && drone.getHedefBoylam() != null) {
            // Hedefe esnek ve kavisli manevralar yaparak ilerleme
            if (drone.getEnlem() < drone.getHedefEnlem()) drone.setEnlem(drone.getEnlem() + hizX);
            if (drone.getEnlem() > drone.getHedefEnlem()) drone.setEnlem(drone.getEnlem() - hizX);

            if (drone.getBoylam() < drone.getHedefBoylam()) drone.setBoylam(drone.getBoylam() + hizY);
            if (drone.getBoylam() > drone.getHedefBoylam()) drone.setBoylam(drone.getBoylam() - hizY);
        } else {
            drone.setEnlem(drone.getEnlem() + hizX);
            drone.setBoylam(drone.getBoylam() + hizY);
        }

        drone.setBataryaSeviyesi(drone.getBataryaSeviyesi() - 2.5);

        if (drone.getBataryaSeviyesi() <= 0) {
            drone.setDurum(DroneStatus.MAINTENANCE);
            drone.setBataryaSeviyesi(0);
        } else if (drone.getBataryaSeviyesi() <= 55.0) {
            drone.setDurum(DroneStatus.RETURNING);
            System.out.println("BİNGO SEVİYESİ: " + drone.getCagriKodu() + " üsse dönüyor!");
        }
    }
}