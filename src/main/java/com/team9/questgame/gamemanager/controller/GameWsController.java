package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameWsController {

    @MessageMapping("/message")
    @SendTo("/topic/message")
    public Message clientLogin(final Message payload) {
        return payload;
    }

}
