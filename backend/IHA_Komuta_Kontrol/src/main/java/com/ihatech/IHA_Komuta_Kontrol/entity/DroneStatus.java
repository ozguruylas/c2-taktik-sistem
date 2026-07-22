package com.ihatech.IHA_Komuta_Kontrol.entity;

public enum DroneStatus {
    IDLE,           // Üste, göreve hazır bekliyor
    IN_MISSION,     // Şu an havada, görevde
    RETURNING,      // Kritik batarya veya görev sonu, üsse dönüyor
    MAINTENANCE     // Bakımda, uçamaz
}