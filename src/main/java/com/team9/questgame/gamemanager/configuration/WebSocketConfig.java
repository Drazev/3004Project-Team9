package com.team9.questgame.gamemanager.configuration;

import com.team9.questgame.gamemanager.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@AllArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Autowired
//    SessionService sessionService;

    PlayerHandshakeHandler playerHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");   // Outbound message from server to client
        registry.setApplicationDestinationPrefixes("/app");             // Inbound message from client to server will be routed to the "/request" handler
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Setup endpoint for client to initially  make HTTP request for handshake
        registry.addEndpoint("/quest-game-websocket")
//                .setHandshakeHandler(new PlayerHandshakeHandler(sessionService))
                .setHandshakeHandler(playerHandshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
