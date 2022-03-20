package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.rest.SetupCompleteRequest;
import com.team9.questgame.gamemanager.record.rest.SetupCompleteResponse;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quest")
@CrossOrigin(origins = "http://localhost:3000")
public class QuestPhaseRestController {
    private Logger LOG = LoggerFactory.getLogger(QuestPhaseRestController.class);
    @Autowired
    private QuestPhaseInboundService inboundService;

    @PostMapping("/setup-complete")
    public SetupCompleteResponse handleSetupComplete(@RequestBody SetupCompleteRequest setupCompleteRequest) {
        LOG.info("Received from /setup-complete: " + setupCompleteRequest);
        boolean confirmed = inboundService.questSetupComplete(setupCompleteRequest.name());
        return new SetupCompleteResponse(confirmed, setupCompleteRequest.name(), setupCompleteRequest.playerID());
    }
}
