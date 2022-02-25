package com.team9.questgame.Entities.cards;

public enum StoryDeckCards implements DeckCards {
    CHIVALROUS_DEED(CardTypes.EVENT),
    POX(CardTypes.EVENT),
    PLAGUE(CardTypes.EVENT),
    KINGS_RECOGNITION(CardTypes.EVENT),
    QUEENS_FAVOR(CardTypes.EVENT),
    COURT_CALLED_TO_CAMELOT(CardTypes.EVENT),
    KINGS_CALL_TO_ARMS(CardTypes.EVENT),
    PROSPERITY_THROUGHOUT_THE_REALM(CardTypes.EVENT),
    JOURNEY_THROUGH_THE_ENCHANTED_FOREST(CardTypes.QUEST),
    VANQUISH_KING_ARTHURS_ENEMIES(CardTypes.QUEST),
    REPEL_THE_SAXON_RAIDERS(CardTypes.QUEST),
    BOAR_HUNT(CardTypes.QUEST),
    SEARCH_FOR_THE_QUESTING_BEAST(CardTypes.QUEST),
    DEFEND_THE_QUEENS_HONOR(CardTypes.QUEST),
    SLAY_THE_DRAGON(CardTypes.QUEST),
    RESCUE_THE_FAIR_MAIDEN(CardTypes.QUEST),
    SEARCH_FOR_THE_HOLY_GRAIL(CardTypes.QUEST),
    TEST_OF_THE_GREEN_KNIGHT(CardTypes.QUEST),
    TOURNAMENT_AT_CAMELOT(CardTypes.TOURNAMENT),
    TOURNAMENT_AT_ORKNEY(CardTypes.TOURNAMENT),
    TOURNAMENT_AT_TINTAGEL(CardTypes.TOURNAMENT),
    TOURNAMENT_AT_YORK(CardTypes.TOURNAMENT)
    ;

    public final CardTypes subType;

    public CardTypes getSubType() {
        return subType;
    }

    StoryDeckCards(CardTypes subType) {
        this.subType = subType;
    }
}
