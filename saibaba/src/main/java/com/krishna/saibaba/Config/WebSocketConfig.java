package com.krishna.saibaba.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ Native WebSocket endpoint (no SockJS — avoids deprecation warning)
        registry.addEndpoint("/ws-location")
                .setAllowedOriginPatterns("*");

        // ✅ SockJS fallback for older browsers
        registry.addEndpoint("/ws-location-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
