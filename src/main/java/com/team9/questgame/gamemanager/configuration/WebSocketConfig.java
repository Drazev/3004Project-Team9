package com.team9.questgame.gamemanager.configuration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@AllArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private PlayerHandshakeHandler playerHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");   // Outbound message from server to client
        registry.setApplicationDestinationPrefixes("/app");             // Inbound message from client to server will be routed to the "/request" handler
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Setup endpoint for client to initially  make HTTP request for handshake
        registry.addEndpoint("/quest-game-websocket")
                .setHandshakeHandler(playerHandshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }


}
