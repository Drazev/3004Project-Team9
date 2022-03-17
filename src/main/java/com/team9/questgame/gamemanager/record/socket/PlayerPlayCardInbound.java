package com.team9.questgame.gamemanager.record.socket;

public record PlayerPlayCardInbound(String name, long cardId, long playerID) { }
