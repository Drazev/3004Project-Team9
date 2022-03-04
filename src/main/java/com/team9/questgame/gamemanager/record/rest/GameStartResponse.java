package com.team9.questgame.gamemanager.record.rest;

/**
 * Response body for GET /status and POST /start-game
 */
public record GameStartResponse(boolean gameStarted) {
}
