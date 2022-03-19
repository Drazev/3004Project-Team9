package com.team9.questgame.gamemanager.record.socket;

/*
 * Payload body for sponsor setup stage(indicating sponsor has setup the stage)
 */
public record SponsorSetupStage(String name, boolean complete) {
}