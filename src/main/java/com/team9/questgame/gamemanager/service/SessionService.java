package com.team9.questgame.gamemanager.service;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;


@ToString
@AllArgsConstructor
@Service
public class SessionService {
    @Autowired
    private ConcurrentHashMap<String, String> sessionMap;

    /**
     * Register a new player
     * @param name name of the player
     * @return true if the name is successfully registered, false if name already exists
     */
    public synchronized boolean registerPlayer(String name) {
        if (sessionMap.containsKey(name)) {
            return false;
        } else {
            sessionMap.put(name, "");
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
     * @return all players' name
     */
    public ArrayList<String> getPlayers() {
        ArrayList<String> players = new ArrayList<>();

        for (String name: sessionMap.keySet()) {
            players.add(name);
        }

        return players;
    }

    /**
     * Get the number of player registered
     * @return the number of player registered
     */
    public int getNumberOfPlayers() {
        return sessionMap.size();
    }

    /**
     * Get a player information
     * @param name the name of the player
     * @return the player information if the player exists, else return null
     */
    private String getPlayer(String name) {
        return sessionMap.getOrDefault(name, null);
    }
}
