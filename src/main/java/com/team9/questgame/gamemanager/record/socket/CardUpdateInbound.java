package com.team9.questgame.gamemanager.record.socket;

/**
 * Payload body for Card draw and discard /
 */
public record CardUpdateInbound(String name, long cardId) {
}
