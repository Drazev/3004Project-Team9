package com.team9.questgame.gamemanager.service;
import com.team9.questgame.Data.*;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import com.team9.questgame.gamemanager.record.socket.PlayerPlayCardInbound;
import com.team9.questgame.gamemanager.record.socket.SponsorPlayCardInbound;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class QuestPhaseInboundService implements ApplicationContextAware {
    private Logger LOG;
    static private ApplicationContext context;

    @Autowired
    private SessionService sessionService;


    @Autowired
    private QuestPhaseOutboundService outboundService;

    private QuestPhaseController questController;

    public QuestPhaseInboundService() {
        this.LOG = LoggerFactory.getLogger(QuestPhaseInboundService.class);
    }

    public void setQuestController(QuestPhaseController controller) {
        questController = controller;
    }
    public synchronized void checkSponsorResult(String name, boolean found){
        if(questController==null) {
            throw new RuntimeException("No active quest Phase set on QuestPhaseInboundService");
        }
        LOG.info(String.format("Receiving Sponsor Result: name=%s, found=%s", name, found));
        Players player = sessionService.getPlayerMap().get(name);
        //System.out.println("session service get player: name =" + player.getName());
        questController.checkSponsorResult(player, found);
        //return found;
    }

    public synchronized boolean questSetupComplete(String name){
        if(questController==null) {
            throw new RuntimeException("No active quest Phase set on QuestPhaseInboundService");
        }
        LOG.info(String.format("Notification that sponsor has completed stage setup: name=%s", name));
        Players player = sessionService.getPlayerMap().get(name);
        if (player == null) {
            throw new NullPointerException(String.format("Player with name=%s not found", name));
        }

        return questController.questSetupComplete(player);
    }

    public synchronized void checkJoinResult(String name, boolean joined){
        if(questController==null) {
            throw new RuntimeException("No active quest Phase set on QuestPhaseInboundService");
        }
        LOG.info(String.format("JOIN RESULT: %s joined? %s", name, joined));
        Players player = sessionService.getPlayerMap().get(name);
        questController.checkJoinResult(player, joined);
    }

    public synchronized  void checkParticipantSetup(String name){
        if(questController==null) {
            throw new RuntimeException("No active quest Phase set on QuestPhaseInboundService");
        }
        Players player = sessionService.getPlayerMap().get(name);
        questController.checkParticipantSetup(player);
    }

    public synchronized void sponsorPlayCard(SponsorPlayCardInbound sponsorPlayCardInbound) {
        if(questController==null) {
            throw new RuntimeException("No active quest Phase set on QuestPhaseInboundService");
        }
        Players player = sessionService.getPlayerByPlayerId(sponsorPlayCardInbound.playerID());
        questController.sponsorPlayCard(player, sponsorPlayCardInbound.cardId(),sponsorPlayCardInbound.src(), sponsorPlayCardInbound.dst());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }

    static public QuestPhaseInboundService getService() {
        return context.getBean(QuestPhaseInboundService.class);
    }
}
