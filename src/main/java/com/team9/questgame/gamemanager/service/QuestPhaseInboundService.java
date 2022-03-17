package com.team9.questgame.gamemanager.service;
import com.team9.questgame.Data.*;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class QuestPhaseInboundService {
    private Logger LOG;

    @Autowired
    private SessionService sessionService;


    @Autowired
    private QuestPhaseOutboundService outboundService;

    @Autowired
    private QuestPhaseController questController;

    public QuestPhaseInboundService() {
        this.LOG = LoggerFactory.getLogger(InboundService.class);
    }


    public synchronized void checkSponsorResult(String name, boolean found){
//        if(found){
//            //TODO:
//        }
//        questController.noSponsor();
//        return found;
        LOG.info(String.format("Receiving Sponsor Result: name=%s, found=%s", name, found));
        Players player = sessionService.getPlayerMap().get(name);
        System.out.println("session service get player: name =" + player.getName());
        questController.checkSponsorResult(player, found);
        //return found;
    }

    public synchronized void  sponsorSetupStage(String name, boolean complete){
        LOG.info(String.format("Notification that sponsor has completed stage setup: name=%s, complete=%s", name, complete));
        //Players player = sessionService.getPlayerMap().get(name);
        questController.stageSetupComplete(complete);
    }

    public synchronized void checkJoinResult(String name, boolean joined){

        Players player = sessionService.getPlayerMap().get(name);
        questController.checkJoinResult(player, joined);
    }

}
