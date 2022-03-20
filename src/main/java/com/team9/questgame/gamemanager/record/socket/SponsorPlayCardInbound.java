package com.team9.questgame.gamemanager.record.socket;

public record SponsorPlayCardInbound(
        String name,
        long playerID,
        long cardId,
        int src,
        int dst
) {
}
