package com.team9.questgame.gamemanager.service;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@ToString
@Service
@Data
public class SessionService {

    private final ConcurrentHashMap<String, String> sessionMap;

    public SessionService() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    /**
     * Register a new player
     * @param name name of the player
     * @return true if the name is successfully registered, false if name already exists
     */
    public synchronized boolean registerPlayer(String name) {
        if (sessionMap.containsKey(name)) {
            return false;
        } else {
            String randomId = UUID.randomUUID().toString();
            sessionMap.put(name, randomId);
            return true;
        }
    }

    /**
     * Deregister a player
     * @param name name of the player
     * @return true if the player is de-registered successfully, false if the player doesn't exist
     */
    public synchronized boolean deregisterPlayer(String name) {
        if (!sessionMap.containsKey(name)) {
            return false;
        } else {
            sessionMap.remove(name);
            return true;
        }
    }

    /**
     * Get all players' information
     * @return all players' name and sessionId
     */
    public synchronized Map<String, String> getPlayers() {
        return new HashMap<>(sessionMap);
    }

    /**
     * Get a player's session ID
     * @param name name of the player
     * @return their sessionId
     */
    public synchronized String getPlayerSessionId(String name) {
        return sessionMap.getOrDefault(name, null);
    }

    /**
     * Get the number of player registered
     * @return the number of player registered
     */
    public synchronized int getNumberOfPlayers() {
        return sessionMap.size();
    }

    /**
     * Get a player information
     * @param name the name of the player
     * @return the player information if the player exists, else return null
     */
    private synchronized String getPlayer(String name) {
        return sessionMap.getOrDefault(name, null);
    }
}
