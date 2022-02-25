package com.team9.questgame.Entities.cards;

public abstract class Cards {
    protected String activeAbilityDescription;
    protected String cardName;
    protected long cardID;
    protected final CardTypes subType;
    protected String imageFileName;
    protected static long nextId=0;
    protected AdventureDeckCards cardCode;

    public Cards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode) {
        this.cardID = nextId++;

        this.activeAbilityDescription = activeAbilityDescription;
        this.cardName = cardName;
        this.subType = subType;
        this.imageFileName = imageFileName;
        this.cardCode = cardCode;
    }
}
