package com.team9.questgame.gamemanager.record.socket;

public record JoinResponseInbound(String name,long playerID, boolean joined) {
}
