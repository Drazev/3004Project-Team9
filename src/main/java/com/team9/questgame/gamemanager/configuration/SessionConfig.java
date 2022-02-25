package com.team9.questgame.gamemanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionConfig {
    @Bean
    public ConcurrentHashMap<String, String> getSessionMap() {
        return new ConcurrentHashMap<>();
    }
}
