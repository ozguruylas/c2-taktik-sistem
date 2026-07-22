package com.ihatech.IHA_Komuta_Kontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Bu sihirli kelime, zamanlanmış (sürekli dönen) döngüleri aktif eder.
public class IhaKomutaKontrolApplication {
	public static void main(String[] args) {
		SpringApplication.run(IhaKomutaKontrolApplication.class, args);
	}
}