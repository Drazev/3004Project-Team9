package com.team9.questgame.Entities.cards;

public enum AdventureDeckCards implements DeckCards {
    EXCALIBUR(CardTypes.WEAPON),
    LANCE(CardTypes.WEAPON),
    BATTLE_AX(CardTypes.WEAPON),
    SWORD(CardTypes.WEAPON),
    HORSE(CardTypes.WEAPON),
    DAGGER(CardTypes.WEAPON),
    TEST_OF_THE_QUESTING_BEAST(CardTypes.TEST),
    TEST_OF_TEMPTATION(CardTypes.TEST),
    TEST_OF_VALOR(CardTypes.TEST),
    TEST_OF_MORGAN_LE_FEY(CardTypes.TEST),
    QUEEN_ISEULT(CardTypes.ALLY),
    SIR_LANCELOT(CardTypes.ALLY),
    SIR_GALAHAD(CardTypes.ALLY),
    SIR_GAWAIN(CardTypes.ALLY),
    KING_PELLINORE(CardTypes.ALLY),
    SIR_PERCIVAL(CardTypes.ALLY),
    SIR_TRISTAN(CardTypes.ALLY),
    KING_ARTHUR(CardTypes.ALLY),
    QUEEN_GUINEVERE(CardTypes.ALLY),
    MERLIN(CardTypes.ALLY),
    AMOUR(CardTypes.AMOUR),
    MORDRED(CardTypes.FOE),
    GIANT(CardTypes.FOE),
    DRAGON(CardTypes.FOE),
    THIEVES(CardTypes.FOE),
    BOAR(CardTypes.FOE),
    SAXONS(CardTypes.FOE),
    ROBBER_KNIGHT(CardTypes.FOE),
    GREEN_KNIGHT(CardTypes.FOE),
    BLACK_KNIGHT(CardTypes.FOE),
    EVIL_KNIGHT(CardTypes.FOE),
    SAXON_KNIGHT(CardTypes.FOE)
    ;

    final CardTypes subType;

    AdventureDeckCards(CardTypes subType) {
        this.subType = subType;
    }

    public CardTypes getSubType() {
        return subType;
    }
}
