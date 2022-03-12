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
    private QuestPhaseOutboundService outboundService;

    @Autowired
    private QuestPhaseController questController;

    public QuestPhaseInboundService() {
        this.LOG = LoggerFactory.getLogger(InboundService.class);
    }


    public synchronized boolean checkSponsorResult(Players player, boolean found){
//        if(found){
//            //TODO:
//        }
//        questController.noSponsor();
//        return found;
        questController.checkSponsorResult(player, found);
        return found;
    }

}
