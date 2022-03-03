package com.team9.questgame.Data;

import com.team9.questgame.Entities.PlayerRanks;

import java.util.ArrayList;


public record PlayerData(
        long playerId,
        String name,
        PlayerRanks rank,
        int rankBattlePoints,
        int shields,
        boolean isHandOversize,
        ArrayList<CardData> hand
) {}
