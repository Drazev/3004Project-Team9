package com.team9.questgame.gamemanager.controller;


import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.gamemanager.model.*;
import com.team9.questgame.gamemanager.service.GameService;
import com.team9.questgame.gamemanager.service.SessionService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class GameRestController {
    private Logger LOG = LoggerFactory.getLogger(GameRestController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GameService gameService;

    /**
     * Register a player to the game
     * @param requestBody contains the information of the player to be registered
     * @return a response containing the confirmation
     */
    @PostMapping("/register")
    public RegistrationResponse handleRegister(@RequestBody RegistrationRequest requestBody) {
        LOG.info(String.format("POST /api/register: requestBody = %s", requestBody));
        boolean registrationConfirmed = sessionService.registerPlayer(requestBody.getName());
        if (registrationConfirmed) {
            gameService.broadcastPlayerConnect();
        }
        return new RegistrationResponse(registrationConfirmed, requestBody.getName());
    }

    /**
     * De-register a player from the game
     * @param requestBody contains the information of the player to be de-registered
     * @return a response containing the confirmation
     */
    @DeleteMapping("/register")
    public RegistrationResponse handleDeregister(@RequestBody Optional<RegistrationRequest> requestBody, @RequestParam Optional<String> name) {
        LOG.info(String.format("DELETE /api/register: requestBody = %s, requestParams = %s", requestBody, name));

        boolean confirmed;
        String requestName;
        if (name.isPresent()) {
            requestName = name.get();
        } else if (requestBody.isPresent()) {
            requestName = requestBody.get().getName();
        } else {
            throw new BadRequestException("No name included in De-registration request");
        }

        confirmed = sessionService.deregisterPlayer(requestName);

        if (confirmed) {
            gameService.broadcastPlayerDisconnect();
        }

        return new RegistrationResponse(confirmed, requestName);
    }

    /**
     * Get all registered player
     * @return a response containing the registered players
     */
    @GetMapping("/player")
    public Map<String, String> handleGetPlayers() {
        LOG.info("GET /api/player");
        return sessionService.getPlayers();
    }

    /**
     * Handle request to start the game from client
     * @return a response containing the game status
     */
    @PostMapping("/start")
    public GameStartResponse handleGameStart() {
        LOG.info("POST /api/start");
        boolean gameStarted = gameService.startGame();

        return new GameStartResponse(gameStarted);
    }

    /**
     * Handle get request for game status
     * @return a response containing the game status
     */
    @GetMapping("/start")
    public GameStartResponse handleGameStatus() {
        LOG.info("GET /api/start");
        boolean gameStarted = gameService.isGameStarted();
        return new GameStartResponse(gameStarted);
    }

}
