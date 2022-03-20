package com.team9.questgame.gamemanager.record.socket;

/*
* Payload body for response to sponsor request
*/
public record SponsorResponseInbound(String name, long playerID, boolean found) {
}
