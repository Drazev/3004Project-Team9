package com.team9.questgame;

import com.team9.questgame.payload.MessagePayload;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/message")
    @SendTo("/topic/message")
    public MessagePayload clientLogin(MessagePayload payload) {
        return payload;
    }
}
