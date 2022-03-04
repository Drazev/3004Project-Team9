package com.team9.questgame.gamemanager.record.rest;

/**
 * Response body for POST/DELETE /api/register
 */
public record RegistrationResponse(boolean confirmed, String name) {
}
