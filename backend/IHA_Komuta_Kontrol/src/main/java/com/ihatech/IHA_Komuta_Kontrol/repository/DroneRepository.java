package com.ihatech.IHA_Komuta_Kontrol.repository;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    // Spring Data JPA Sihri: Sadece metod ismini İngilizce kurallarına göre yazarak arka planda SQL sorgusu üretiriz.

    // 1. Göreve hazır (IDLE) olan tüm İHA'ları bulmak için:
    List<Drone> findByDurum(DroneStatus durum);

    // 2. Belirli bir batarya seviyesinin üzerindeki ve boşta olan (IDLE) İHA'ları bulmak için (Görev atama zekası):
    List<Drone> findByDurumAndBataryaSeviyesiGreaterThan(DroneStatus durum, double bataryaSeviyesi);

    // 3. Çağrı koduna göre spesifik bir uçağı bulmak için (Örn: "TB2-Alpha" nerede?):
    Drone findByCagriKodu(String cagriKodu);
}