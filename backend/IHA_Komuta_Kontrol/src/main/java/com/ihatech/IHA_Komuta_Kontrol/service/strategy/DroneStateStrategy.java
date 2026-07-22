package com.ihatech.IHA_Komuta_Kontrol.service.strategy;

import com.ihatech.IHA_Komuta_Kontrol.entity.Drone;
import com.ihatech.IHA_Komuta_Kontrol.entity.DroneStatus;

public interface DroneStateStrategy {
    // 1. Bu strateji senin durumun için geçerli mi? (Evet/Hayır)
    boolean supports(DroneStatus status);

    // 2. Geçerliyse, fizik kurallarını ve zekayı çalıştır!
    void execute(Drone drone);
}