package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.socket.CardUpdateInbound;
import com.team9.questgame.gamemanager.record.socket.JoinResponseInbound;
import com.team9.questgame.gamemanager.record.socket.SponsorResponseInbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QuestPhaseWsController {
    @Autowired
    private QuestPhaseInboundService inboundService;

    @MessageMapping("/quest/sponsor-response")
    private void handleSponsorResponse(SponsorResponseInbound sponsorResponseInbound){
        inboundService.checkSponsorResult( sponsorResponseInbound.name(), sponsorResponseInbound.found());
    }

    @MessageMapping("/quest/join-response")
    private void handleJoinResponse(JoinResponseInbound joinResponseInbound){
        inboundService.checkJoinResult(joinResponseInbound.name(), joinResponseInbound.joined());
    }
}
