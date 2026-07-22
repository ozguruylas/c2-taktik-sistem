package com.ihatech.IHA_Komuta_Kontrol.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drones")
@Data // Lombok: Tüm getter/setter/toString metodlarını otomatik yazar
@NoArgsConstructor // Lombok: Boş constructor oluşturur (JPA için zorunlu)
@AllArgsConstructor // Lombok: Tüm alanları içeren constructor oluşturur

public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Veri tabanı için benzersiz kimlik (Primary Key)

    @Column(unique = true, nullable = false)
    private String cagriKodu; // Örn: "TB2-Alpha" (Sistemde iki tane aynı kodlu İHA olamaz)
    private String model; // Örn: "Bayraktar TB2", "Akıncı"

    private double bataryaSeviyesi; // Yüzde olarak: 100.0, 15.5

    // Harita ve Simülasyon için Telemetri Verileri
    private double enlem;  // Latitude (X koordinatı)
    private double boylam; // Longitude (Y koordinatı)
    private double irtifa; // Yükseklik (Metre)
    private double hiz;    // Anlık hız (Knot)

    // Sonradan eklediklerim
    private Double hedefEnlem;
    private Double hedefBoylam;

    @Enumerated(EnumType.STRING)
    private DroneStatus durum; // İHA'nın anlık statüsü
}