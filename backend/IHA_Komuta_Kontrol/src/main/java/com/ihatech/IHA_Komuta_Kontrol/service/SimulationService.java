package com.ihatech.IHA_Komuta_Kontrol.service;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.repository.DroneRepository;
import com.ihatech.IHA_Komuta_Kontrol.service.strategy.DroneStateStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final DroneRepository droneRepository;
    private final List<DroneStateStrategy> stateStrategies;

    // ==========================================
    // YENİ EKLENEN: WebSocket Yayıncımız (Broadcaster)
    // ==========================================
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 3000)
    public void ucaklariHareketEttir() {
        List<Drone> dronelar = droneRepository.findAll();

        for (Drone drone : dronelar) {

            // OTONOM ZEKA MOTORU
            for (DroneStateStrategy strategy : stateStrategies) {
                if (strategy.supports(drone.getDurum())) {
                    strategy.execute(drone);
                    break;
                }
            }

            droneRepository.save(drone);
        }

        // ==========================================
        // YENİ EKLENEN: Canlı Radar Yayını!
        // Tüm hesaplamalar ve veritabanı kayıtları bitti.
        // Şimdi güncel İHA listesini "/topic/drones" frekansına fırlatıyoruz.
        // ==========================================
        messagingTemplate.convertAndSend("/topic/drones", dronelar);
    }
}