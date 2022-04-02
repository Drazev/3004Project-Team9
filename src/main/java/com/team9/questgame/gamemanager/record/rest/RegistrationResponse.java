package com.team9.questgame.gamemanager.record.rest;

import com.team9.questgame.Data.PlayerData;

/**
 * Response body for POST/DELETE /api/register
 */
public record RegistrationResponse(boolean confirmed, String name, PlayerData playerData) {
}
