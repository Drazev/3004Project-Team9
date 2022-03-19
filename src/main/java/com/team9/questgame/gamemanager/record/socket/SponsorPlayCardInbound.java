package com.team9.questgame.gamemanager.record.socket;

public record SponsorPlayCardInbound(
        String name,
        long cardId,
        long playerID,
        int src,
        int dst
) {
}
