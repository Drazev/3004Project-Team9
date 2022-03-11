package com.team9.questgame.gamemanager.service;

import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class OutboundService {
    private Logger LOG;

    @Autowired
    private SimpMessagingTemplate messenger;

    @Autowired
    private SessionService sessionService;

    public OutboundService() {
        this.LOG = LoggerFactory.getLogger(OutboundService.class);
    }

    /**
     * Broadcast player-draw-connect event
     */
    public void broadcastPlayerConnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-connect", players);
    }

    public void broadcastPlayerDisconnect() {
        Map<String, String> players = sessionService.getPlayers();
        this.sendToAllPlayers("/topic/general/player-disconnect", players);
    }

    public void broadcastGameStart() {
        // @TODO: include player turn in the next iteration
        this.sendToAllPlayers("/topic/general/game-start");
    }

    public void broadcastNextTurn() {
        // @TODO: include player turn in the next iteration
        this.sendToAllPlayers("/topic/general/next-turn");
    }

    public void broadcastPlayerHandUpdate(PlayerData playerData) {
        HandUpdateOutbound handUpdate = new HandUpdateOutbound(playerData.name(), playerData.hand());
        this.sendToAllPlayers("/topic/player/hand-update", handUpdate);
    }

    public void broadcastPlayerRankUpdate(PlayerData playerData) {
        this.sendToAllPlayers("/topic/player/rank-up", playerData);
    }

    private void sendToPlayer(String topic, Players player, Object payload) {
        LOG.info(String.format("Broadcasting to one player: topic=%s, name=%s, payload=%s", topic, player.getName(), payload));
        messenger.convertAndSendToUser(topic, sessionService.getPlayerSessionId(player.getName()), payload);
    }

    public void sendHandOversize(Players player) {
        this.sendToPlayer("/topic/player/hand-oversize",sessionService.getPlayerSessionId(player.getName()),new EmptyJsonReponse());
    }

    private void sendToPlayer(String topic, String name, Object payload) {
        LOG.info(String.format("Broadcasting to one player: topic=%s, name=%s, payload=%s", topic, name, payload));
        messenger.convertAndSendToUser(topic, sessionService.getPlayerSessionId(name), payload);
    }

    private void sendToAllPlayers(String topic, Object payload) {
        LOG.info(String.format("Broadcasting to all players: topic=%s, payload=%s", topic, payload));
        messenger.convertAndSend(topic, payload);
    }

    private void sendToAllPlayers(String topic) {
        LOG.info(String.format("Broadcasting to one players: topic=%s", topic));
        messenger.convertAndSend(topic, new EmptyJsonReponse());
    }
}
