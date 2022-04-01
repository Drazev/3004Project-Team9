package com.team9.questgame.Data;

import com.team9.questgame.Entities.PlayerRanks;

import java.util.ArrayList;


public record PlayerData(
        long playerId,
        long handId,
        long playAreaId,
        String name,
        PlayerRanks rank,
        String rankImgSrc,
        int rankBattlePoints,
        int shields
) {}
