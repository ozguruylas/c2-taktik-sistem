package com.ihatech.IHA_Komuta_Kontrol.controller;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Bu sınıfın bir REST API uç noktası olduğunu belirtir
@RequestMapping("/api/drones") // Bu kapının ana adresi (URL)
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DroneController {

    // İsteği karşıladıktan sonra işi zekaya (Service) devretmek için bağımlılığı çağırıyoruz
    private final DroneService droneService;

    // 1. Yeni İHA Ekleme (Dışarıdan veri geleceği için POST isteği kullanılır)
    @PostMapping("/ekle")
    public ResponseEntity<Drone> sistemeIhaEkle(@RequestBody Drone drone) {
        // @RequestBody, dışarıdan gelen JSON verisini Java nesnesine (Drone) çevirir
        Drone kaydedilenDrone = droneService.sistemeIhaEkle(drone);
        return ResponseEntity.ok(kaydedilenDrone); // 200 OK mesajıyla birlikte kaydedilen İHA'yı geri dön
    }

    // 2. Tüm Filoyu Getirme (Sadece veri okunacağı için GET isteği kullanılır)
    @GetMapping("/filo")
    public ResponseEntity<List<Drone>> tumFiloyuGetir() {
        return ResponseEntity.ok(droneService.tumFiloyuGetir());
    }

    // 3. Çağrı Koduna Göre İHA Bulma (URL'den değişken almak için @PathVariable kullanılır)
    @GetMapping("/bul/{cagriKodu}")
    public ResponseEntity<Drone> cagriKoduIleBul(@PathVariable String cagriKodu) {
        return ResponseEntity.ok(droneService.cagriKoduIleBul(cagriKodu));
    }

    // 4. Görev İçin Uygun İHA Bulma
    @GetMapping("/gorev-icin-uygun")
    public ResponseEntity<Drone> gorevIcinUygunIhaBul() {
        return ResponseEntity.ok(droneService.gorevIcinUygunIhaBul());
    }

    // 5. Operatör Manuel RTB Emri
    @PostMapping("/{id}/rtb")
    public ResponseEntity<Drone> acilRtbEmri(@PathVariable Long id) {
        // Service katmanındaki mevcut metodumuzu kullanarak uçağın durumunu zorla RETURNING yapıyoruz
        Drone guncellenenDrone = droneService.durumGuncelle(id, com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus.RETURNING);
        return ResponseEntity.ok(guncellenenDrone);
    }
}