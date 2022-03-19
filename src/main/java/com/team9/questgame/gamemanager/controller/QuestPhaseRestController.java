package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.rest.SetupCompleteRequest;
import com.team9.questgame.gamemanager.record.rest.SetupCompleteResponse;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quest")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestPhaseRestController {
    @Autowired
    private QuestPhaseInboundService inboundService;

    @PostMapping("/setup-complete")
    public SetupCompleteResponse handleSetupComplete(SetupCompleteRequest setupCompleteRequest) {
        boolean confirmed = inboundService.questSetupComplete(setupCompleteRequest.name());
        return new SetupCompleteResponse(confirmed, setupCompleteRequest.name(), setupCompleteRequest.playerID());
    }

}
