package com.ihatech.IHA_Komuta_Kontrol.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - DOMAIN ENTITY
 * ============================================================================
 * Architecture : JPA Entity / Database Model
 * Purpose      : Represents a physical Unmanned Aerial Vehicle (UAV) asset
 *                within the operational grid. Holds critical telemetry,
 *                specifications, and real-time state data.
 * ============================================================================
 */
@Entity
@Table(name = "drones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drone {

    // --- 1. Asset Identity & Specifications ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cagriKodu;

    private String model;

    // --- 2. Hardware & Power Systems ---

    private double bataryaSeviyesi;

    // --- 3. Real-Time Telemetry ---

    private double enlem;
    private double boylam;
    private double irtifa;
    private double hiz;

    // --- 4. Navigation & Target Routing ---
    // Note: Used Wrapper Class (Double) to allow null values during free-flight

    private Double hedefEnlem;
    private Double hedefBoylam;

    // --- 5. Operational State ---

    @Enumerated(EnumType.STRING)
    private DroneStatus durum;
}