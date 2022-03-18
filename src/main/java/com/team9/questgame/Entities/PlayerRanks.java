package com.team9.questgame.Entities;

public enum PlayerRanks {
    SQUIRE(5, 0),
    KNIGHT(10, 5),
    CHAMPION_KNIGHT(20, 7),
    KNIGHT_OF_ROUND_TABLE(99, 10)
    ;

    private final int battlePoints;
    private final int rankShieldCost;


    PlayerRanks(int battlePoints, int rankShieldCost) {
        this.battlePoints = battlePoints;
        this.rankShieldCost = rankShieldCost;
    }

    public int getRankBattlePointValue() {
        return battlePoints;
    }

    public int getRankShieldCost() {
        return rankShieldCost;
    }

    public PlayerRanks getNextRank() {
            return switch(this) {
                case SQUIRE->PlayerRanks.KNIGHT;
                case KNIGHT->PlayerRanks.CHAMPION_KNIGHT;
                default->PlayerRanks.KNIGHT_OF_ROUND_TABLE;
            };
    }

    public int getNextRankCost() {
        return getNextRank().getRankShieldCost();
    }
}
