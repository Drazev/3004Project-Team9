package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.model.CardEvent;
import com.team9.questgame.gamemanager.model.GameStartResponse;
import com.team9.questgame.gamemanager.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;


@Controller
public class GameWsController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/general/player-draw-card")
    public void handlePlayerDrawCard(CardEvent cardEvent) {
        gameService.playerDrawCard(cardEvent);
    }

    @MessageMapping("/general/player-discard-card")
    public void handlePlayerDiscardCard(CardEvent cardEvent) {
        gameService.playerDiscardCard(cardEvent);
    }

}
