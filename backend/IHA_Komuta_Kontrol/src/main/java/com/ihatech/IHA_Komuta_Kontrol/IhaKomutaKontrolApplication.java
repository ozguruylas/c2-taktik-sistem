package com.ihatech.IHA_Komuta_Kontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - APPLICATION ENTRY POINT
 * ============================================================================
 * Architecture : Spring Boot Main Context / Bootstrap Class
 * Purpose      : Initializes the Spring IoC container and activates asynchronous
 *                and scheduled task execution for the autonomous simulation engine.
 * ============================================================================
 */
@SpringBootApplication
@EnableScheduling
public class IhaKomutaKontrolApplication {

	/**
	 * Main execution thread. Bootstraps the entire backend application context.
	 *
	 * @param args Command line arguments passed during startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(IhaKomutaKontrolApplication.class, args);
	}
}