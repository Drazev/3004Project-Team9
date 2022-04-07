package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.gamemanager.record.socket.*;
import com.team9.questgame.gamemanager.service.InboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
public class GameWsController {

    @Autowired
    private InboundService inboundService;
    @Autowired
    EffectResolverService effectService;
    private Logger LOG = LoggerFactory.getLogger(GameRestController.class);


    @MessageMapping("/general/player-draw-card")
    public void handlePlayerDrawCard(CardUpdateInbound cardUpdateInbound) {
        inboundService.playerDrawStoryCard(cardUpdateInbound.name(), cardUpdateInbound.cardId());
    }

    @MessageMapping("/general/player-discard-card")
    public void handlePlayerDiscardCard(CardUpdateInbound cardUpdateInbound) {
        inboundService.playerDiscardCard(cardUpdateInbound.name(), cardUpdateInbound.cardId());
    }

    @MessageMapping("/general/player-play-card")
    public void handlePlayerPlayCard(PlayerPlayCardInbound playerPlayCardInbound) {
        LOG.info(String.format("P"));
        inboundService.playerPlayCard(playerPlayCardInbound);
    }

    @MessageMapping("/effects/card-target-selection-response")
    public void handleEffectsCardTargetSelectionResponse(CardTargetSelectionResponse data) {
        LOG.info(String.format("Card Target Received for playerID %d, requestID: %d.Target CardID: %d",data.requestPlayerID(),data.requestID(),data.targetCardID()));
        if(!effectService.handleCardTargetSelectionResponse(data))
        {
            throw new RuntimeException("Card Target Selection data is invalid or malformed");
        }
    }

    @MessageMapping("/effects/stage-target-selection-response")
    public void handleEffectsStageTargetSelectionResponse(StageTargetSelectionResponse data) {
        LOG.info(String.format("Stage Target Received for playerID %d, requestID: %d, targetStageID: %d",data.requestPlayerID(),data.requestID(),data.targetStageID()));
        if(!effectService.handleStageTargetSelectionResponse(data))
        {
            throw new RuntimeException("Stage Target Selection data is invalid or malformed");
        }
    }

    @MessageMapping("/tournament/join-response")
    public void handleTournamentJoinResponse(JoinResponseInbound joinResponseInbound){
        LOG.info(String.format("Competitor %s has decided to join: %s", joinResponseInbound.name(), joinResponseInbound.joined()));
        inboundService.tournamentJoinResponse(joinResponseInbound.name(), joinResponseInbound.joined());
    }

    @MessageMapping("/tournament/setup-complete")
    public void handleTournamentCompetitorSetup(ParticipantSetupStage participantSetupStage){
        LOG.info(String.format("Competitor %s has completed setup for tournament", participantSetupStage.name()));
        inboundService.tournamentCompetitorSetup(participantSetupStage.name());
    }

}
