package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.socket.CardUpdateInbound;
import com.team9.questgame.gamemanager.record.socket.PlayerPlayCardInbound;
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

}
