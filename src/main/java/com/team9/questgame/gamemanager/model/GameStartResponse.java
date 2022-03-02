package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for GET /status and POST /start-game
 */
@Data
@AllArgsConstructor
public class GameStartResponse {
    private boolean gameStarted;
}
