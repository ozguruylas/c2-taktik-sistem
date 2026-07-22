package com.ihatech.IHA_Komuta_Kontrol.service;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;
import com.ihatech.IHA_Komuta_Kontrol.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneService {

    // Dependency Injection (Bağımlılık Enjeksiyonu) burada gerçekleşiyor.
    // Nesneyi biz "new" anahtar kelimesiyle oluşturmuyoruz, Spring Boot bizim yerimize enjekte ediyor.
    private final DroneRepository droneRepository;

    // 1. Yeni İHA Kaydetme
    public Drone sistemeIhaEkle(Drone drone) {
        // İHA ilk eklendiğinde varsayılan olarak "IDLE" (Boşta/Göreve Hazır) statüsünde başlar.
        drone.setDurum(DroneStatus.IDLE);
        return droneRepository.save(drone);
    }

    // 2. Tüm Filoyu Getirme
    public List<Drone> tumFiloyuGetir() {
        return droneRepository.findAll();
    }

    // 3. Çağrı Koduna Göre İHA Bulma
    public Drone cagriKoduIleBul(String cagriKodu) {
        return droneRepository.findByCagriKodu(cagriKodu);
    }

    // 4. İş Mantığı: Görev İçin En Uygun İHA'yı Seçme (Zeka Katmanı)
    public Drone gorevIcinUygunIhaBul() {
        // Kural: Uçak IDLE (Boşta) olmalı VE bataryası %20'den fazla olmalı.
        List<Drone> uygunIhalar = droneRepository.findByDurumAndBataryaSeviyesiGreaterThan(DroneStatus.IDLE, 20.0);

        if (uygunIhalar.isEmpty()) {
            throw new RuntimeException("Uyarı: Görev için uygun, şarjı yeterli veya boşta olan İHA bulunamadı!");
        }

        // Şimdilik listeye giren ilk uygun İHA'yı göreve atıyoruz.
        // İleride buraya "hedefe en yakın olanı seç" gibi daha karmaşık bir algoritma ekleyebiliriz.
        return uygunIhalar.get(0);
    }

    // 5. İHA Durumunu Güncelleme (Örn: Göreve çıktı, Üsse dönüyor)
    public Drone durumGuncelle(Long id, DroneStatus yeniDurum) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İHA bulunamadı!"));

        drone.setDurum(yeniDurum);
        return droneRepository.save(drone);
    }
}