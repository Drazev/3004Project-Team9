package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.socket.*;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QuestPhaseWsController {
    @Autowired
    private QuestPhaseInboundService inboundService;

    private Logger LOG;

    public QuestPhaseWsController() {
        LOG = LoggerFactory.getLogger(QuestPhaseWsController.class);
    }

    @MessageMapping("/quest/sponsor-response")
    private void handleSponsorResponse(SponsorResponseInbound sponsorResponseInbound){
        LOG.info("Received from /quest/sponsor-response: " + sponsorResponseInbound);
        inboundService.checkSponsorResult( sponsorResponseInbound.name(), sponsorResponseInbound.found());
    }

    @MessageMapping("/quest/setup-complete")
    private void handleStageSetupComplete(SponsorSetupStage sponsorSetupStage){
        inboundService.questSetupComplete(sponsorSetupStage.name());
    }

    @MessageMapping("/quest/join-response")
    private void handleJoinResponse(JoinResponseInbound joinResponseInbound){
        inboundService.checkJoinResult(joinResponseInbound.name(), joinResponseInbound.joined());
    }
    @MessageMapping("/quest/sponsor-play-card")
    public void handlePlayerPlayCard(SponsorPlayCardInbound sponsorPlayCardInbound) {
        inboundService.sponsorPlayCard(sponsorPlayCardInbound);
    }

    @MessageMapping("/quest/participant-setup-complete")
    public void handleParticipantSetup(ParticipantSetupStage participantSetupStage) {
        inboundService.checkParticipantSetup(participantSetupStage.name());
    }

    @MessageMapping("/quest/test-bid-response")
    public void handleTestBidResponse(TestBidResponse testBidResponse){
        LOG.info("Received from /quest/test-bid-response: " + testBidResponse);
        inboundService.checkTestBidResponse(testBidResponse.name(), testBidResponse.bidAmount());
    }
}
