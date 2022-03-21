package com.team9.questgame.gamemanager.service;

import com.team9.questgame.Data.*;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import com.team9.questgame.gamemanager.record.socket.QuestEndedOutbound;
import com.team9.questgame.gamemanager.record.socket.RemainingQuestorsOutbound;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
@Service
public class QuestPhaseOutboundService {

    private Logger LOG;

    @Autowired
    private SimpMessagingTemplate messenger;

    @Autowired
    private SessionService sessionService;

    public QuestPhaseOutboundService() {
        this.LOG = LoggerFactory.getLogger(QuestPhaseOutboundService.class);
    }

    private void sendToAllPlayers(String topic) {
        LOG.info(String.format("Broadcasting to one players: topic=%s", topic));
        messenger.convertAndSend(topic, new EmptyJsonReponse());
    }


    private void sendToAllPlayers(String topic, Object payload) {
        LOG.info(String.format("Broadcasting to all players: topic=%s, payload=%s", topic, payload));
        messenger.convertAndSend(topic, payload);
    }

    public void broadcastSponsorSearch(PlayerData playerData){
        LOG.info(String.format("Broadcast sponsor needed"));
        this.sendToAllPlayers("/topic/quest/sponsor-search", playerData);
    }

    public void broadcastSponsorFound(PlayerData playerData){
        LOG.info(String.format("Broadcast new sponsor"));
        this.sendToAllPlayers("/topic/quest/sponsor-found", playerData);
    }
    public void broadcastSponsorSetup(PlayerData playerData){
        LOG.info(String.format("Broadcast sponsor setting up stage"));
        this.sendToAllPlayers("/topic/quest/sponsor-setup", playerData);
    }
    public void broadcastStageChanged(StageAreaData stageAreaData) {
        LOG.info(String.format("Broadcast Stage Data: %s", stageAreaData));
        this.sendToAllPlayers("/topic/quest/stage-area-changed", stageAreaData);
    }

    public void broadcastFoeStageStart(RemainingQuestorsOutbound remainingQuestorsOutbound){
        this.sendToAllPlayers("/topic/quest/foe-stage-start", remainingQuestorsOutbound);
    }

    public void broadcastStageResult(RemainingQuestorsOutbound remainingQuestorsOutbound){
        this.sendToAllPlayers("/topic/quest/stage-end", remainingQuestorsOutbound);
    }

    public void broadcastQuestEnded(QuestEndedOutbound questEndedOutbound){

        this.sendToAllPlayers("/topic/quest/end", questEndedOutbound);
    }

//    public void broadcastParticipantSetup(PlayerData playerData){
//        LOG.info(String.format("Broadcast to Participant %s to setup for quest stage", playerData.name()));
//        this.sendToAllPlayers("/topic/quest/participant-setup");
//    }

    public void broadcastJoinRequest(){
        LOG.info(String.format("Broadcast join request"));
        this.sendToAllPlayers("/topic/quest/join-request");
    }
}
