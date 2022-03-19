package com.team9.questgame.gamemanager.service;

import com.team9.questgame.Entities.Players;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage all session related services
 */
@Data
@Service
public class SessionService implements ApplicationContextAware {

    private final ConcurrentHashMap<String, String> sessionMap;
    private final ConcurrentHashMap<String, Players> playerMap;
    private final ConcurrentHashMap<String,Players> sessionToPlayerMap;
    private final ConcurrentHashMap<Players,String> playerToSessionMap;
    private static ApplicationContext context;

    public SessionService() {
        this.sessionMap = new ConcurrentHashMap<>();
        this.sessionToPlayerMap = new ConcurrentHashMap<>();
        this.playerMap = new ConcurrentHashMap<>();
        playerToSessionMap = new ConcurrentHashMap<>();
        this.context = null;
    }

    /**
     * Register a new player
     *
     * @param name name of the player
     * @return true if the name is successfully registered, false if name already exists
     */
    public synchronized boolean registerPlayer(String name) {
        if (sessionMap.containsKey(name)) {
            return false;
        } else {
            String randomId = UUID.randomUUID().toString();
            Players newPlayer = new Players(name);
            sessionMap.put(name, randomId);
            playerMap.put(name, newPlayer);
            sessionToPlayerMap.put(randomId,newPlayer);
            playerToSessionMap.put(newPlayer,randomId);
            //newPlayer.onGameReset(); //Reset's game state, and brodcasts
            return true;
        }
    }

    /**
     * Deregister a player
     *
     * @param name name of the player
     * @return true if the player is de-registered successfully, false if the player doesn't exist
     */
    public synchronized boolean deregisterPlayer(String name) {
        boolean rc = false;
        if (!sessionMap.containsKey(name)) {
            return false;
        }
        else {
            if(sessionMap.containsKey(name))
            {
                sessionToPlayerMap.remove(sessionMap.get(name));
                sessionMap.remove(name);
                rc=true;

            }
            if(playerMap.containsKey(name)) {
                playerToSessionMap.remove(playerMap.remove(name));
                playerMap.remove(name);
                rc=true;
            }

            return rc;
        }
    }

    /**
     * Get all players' information
     *
     * @return all players' name and sessionId
     */
    public synchronized Map<String, String> getPlayers() {
        return new HashMap<>(sessionMap);
    }

    /**
     * Get a player's session ID
     *
     * @param name name of the player
     * @return their sessionId
     */
    public synchronized String getPlayerSessionId(String name) {
        return sessionMap.getOrDefault(name, null);
    }

    public synchronized String getPlayerSessionId(Players player) { return playerToSessionMap.get(player); }

    public synchronized HashMap<Players,String> getPlayerToSessionIdMap() { return new HashMap<>(playerToSessionMap);}

    public synchronized Players getPlayerByPlayerId(long playerId) {
        for (Players p: playerMap.values()) {
            if (p.getPlayerId() == playerId) {
                return p;
            }
        }
        return null;
    }

    /**
     * Get the number of player registered
     *
     * @return the number of player registered
     */
    public synchronized int getNumberOfPlayers() {
        return sessionMap.size();
    }

    /**
     * Get a player information
     *
     * @param name the name of the player
     * @return the player information if the player exists, else return null
     */
    private synchronized String getPlayer(String name) {
        return sessionMap.getOrDefault(name, null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }

    public static SessionService getService() {
        return context.getBean(SessionService.class);
    }

}
