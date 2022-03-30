package com.team9.questgame.Entities;

public enum PlayerRanks {
    SQUIRE(5, 0,"./Assets/Rank Deck (327x491)/Rank - Squire.png"),
    KNIGHT(10, 5,"./Assets/Rank Deck (327x491)/Rank - Knight.png"),
    CHAMPION_KNIGHT(20, 7,"./Assets/Rank Deck (327x491)/Rank - Champion Knight.png"),
    KNIGHT_OF_ROUND_TABLE(99, 10,"./Assets/Rank Deck (327x491)/Rank - Knight of The Round Table.png")
    ;

    private final int battlePoints;
    private final int rankShieldCost;
    private final String rankImgSrc;


    PlayerRanks(int battlePoints, int rankShieldCost, String rankImgSrc) {
        this.battlePoints = battlePoints;
        this.rankShieldCost = rankShieldCost;
        this.rankImgSrc = rankImgSrc;
    }

    public int getRankBattlePointValue() {
        return battlePoints;
    }

    public int getRankShieldCost() {
        return rankShieldCost;
    }

    public String getRankImgSrc() {
        return rankImgSrc;
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
