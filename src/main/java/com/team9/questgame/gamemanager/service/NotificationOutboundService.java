package com.team9.questgame.gamemanager.service;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import org.apache.el.stream.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Send outbound notifications to clients
 */
@Service
public class NotificationOutboundService implements ApplicationContextAware {

    private Logger LOG;

    @Autowired
    private SimpMessagingTemplate messenger;

    @Autowired
    private SessionService sessionService;

    private static ApplicationContext context;

    public NotificationOutboundService() {
        this.LOG = LoggerFactory.getLogger(NotificationOutboundService.class);
    }

    public void sendGoodNotification(Players sourcePlayer, NotificationOutbound toUser, NotificationOutbound toOthers) {
        sendToPlayer("/topic/notification/good", sourcePlayer, toUser);
        sendToAllExceptPlayer("/topic/notification/good", sourcePlayer, toOthers);
    }

    public void sendBadNotification(Players sourcePlayer, NotificationOutbound toUser, NotificationOutbound toOthers) {
        sendToPlayer("/topic/notification/bad", sourcePlayer, toUser);
        sendToAllExceptPlayer("/topic/notification/bad", sourcePlayer, toOthers);
    }

    public void sendWarningNotification(Players sourcePlayer, NotificationOutbound toUser, NotificationOutbound toOthers) {
        sendToPlayer("/topic/notification/warning", sourcePlayer, toUser);
        sendToAllExceptPlayer("/topic/notification/warning", sourcePlayer, toOthers);
    }

    public void sendInfoNotification(Players sourcePlayer, NotificationOutbound toUser, NotificationOutbound toOthers) {
        sendToPlayer("/topic/notification/info", sourcePlayer, toUser);
        sendToAllExceptPlayer("/topic/notification/info", sourcePlayer, toOthers);
    }

    public void sendDebugNotification(Players sourcePlayer, NotificationOutbound toUser, NotificationOutbound toOthers) {
        sendToPlayer("/topic/notification/debug", sourcePlayer, toUser);
        sendToAllExceptPlayer("/topic/notification/debug", sourcePlayer, toOthers);
    }

    private void sendToPlayer(String topic, Players player, Object payload) {
        LOG.info(String.format("Broadcasting to one player: topic=%s, name=%s, payload=%s", topic, player.getName(), payload));
        messenger.convertAndSendToUser(sessionService.getPlayerSessionId(player), topic, payload);
    }

    private void sendToAllPlayers(String topic, Object payload) {
        LOG.info(String.format("Broadcasting to all players: topic=%s, payload=%s", topic, payload));
        messenger.convertAndSend(topic, payload);
    }

    private void sendToAllPlayers(String topic) {
        LOG.info(String.format("Broadcasting to one players: topic=%s", topic));
        messenger.convertAndSend(topic, new EmptyJsonReponse());
    }

    private void sendToAllExceptPlayer(String topic, Players excludedPlayer, Object payload) {
        if(payload==null) {
            LOG.info(String.format("Selective Broadcast to all players REJECTED due to null payload. Request: {name: "+excludedPlayer.getName()+", PlayerID: "+excludedPlayer.getPlayerId()+"}, Topic: "+topic+" Payload: "+payload));
            return;
        }
        LOG.info(String.format("Selective Broadcast to all players except {name: "+excludedPlayer.getName()+", PlayerID: "+excludedPlayer.getPlayerId()+"}, Topic: "+topic+" Payload: "+payload));
        for(Map.Entry<Players,String> e : sessionService.getPlayerToSessionIdMap().entrySet()) {
            if(e.getKey()!=excludedPlayer) {
                messenger.convertAndSendToUser(e.getValue(),topic,payload);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    static public NotificationOutboundService getService() {
        return context.getBean(NotificationOutboundService.class);
    }
}
