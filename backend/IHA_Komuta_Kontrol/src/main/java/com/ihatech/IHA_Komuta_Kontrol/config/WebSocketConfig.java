package com.ihatech.IHA_Komuta_Kontrol.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * ============================================================================
 * C2 TACTICAL COMMAND CENTER - REAL-TIME COMMUNICATIONS (WEBSOCKET)
 * ============================================================================
 * Architecture : Event-Driven, STOMP Protocol over SockJS
 * Purpose      : Establishes a persistent, low-latency duplex connection
 *                between the backend simulation engine and the tactical UI.
 *                Acts as the main "radio tower" for broadcasting UAV telemetry.
 * ============================================================================
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Primary Uplink Configuration: Defines the handshake endpoint for the client.
     * Utilizes SockJS fallback options for legacy or restricted network environments.
     *
     * @param registry Spring's STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-radar")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Message Broker Routing: Configures the internal message routing architecture.
     *
     * - "/topic" : Broadcasting frequency for out-bound telemetry (Server to Client).
     * - "/app"   : Prefix for in-bound operator commands (Client to Server).
     *
     * @param registry Spring's message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}