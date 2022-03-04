package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.socket.CardUpdateInbound;
import com.team9.questgame.gamemanager.service.InboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
public class GameWsController {

    @Autowired
    private InboundService inboundService;

    @MessageMapping("/general/player-draw-card")
    public void handlePlayerDrawCard(CardUpdateInbound cardUpdateInbound) {
        inboundService.playerDrawCard(cardUpdateInbound.name(), cardUpdateInbound.cardId());
    }

    @MessageMapping("/general/player-discard-card")
    public void handlePlayerDiscardCard(CardUpdateInbound cardUpdateInbound) {
        inboundService.playerDiscardCard(cardUpdateInbound.name(), cardUpdateInbound.cardId());
    }

}
