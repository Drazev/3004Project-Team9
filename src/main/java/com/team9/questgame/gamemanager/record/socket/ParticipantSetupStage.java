package com.team9.questgame.gamemanager.record.socket;

/*
 * Payload body for participant setup stage
 * (indicating participant finished setting up player area for stage)
 */
public record ParticipantSetupStage(String name, long playerID) {
}