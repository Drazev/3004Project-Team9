package com.team9.questgame.Entities.cards;

public class AllyCards extends Cards implements AdventureCards {
    private final int bonusBp;
    private final int bids;
    private final int boostBonusBp;
    private final int boostBids;
    boolean isBoosted;
    private final boolean isQuestBoosted;
    private final boolean isCardBoosted;
    private final AdventureDeckCards boostCardConditionCode;
    private final StoryDeckCards boostQuestConditionCode;

    public AllyCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int bonusBp, int bids) {
        this(activeAbilityDescription, cardName, subType, imageFileName, cardCode,bonusBp,bids,0,0,null,null);
    }

    public AllyCards(String activeAbilityDescription, String cardName, CardTypes subType, String imageFileName, AdventureDeckCards cardCode, int bonusBp, int bids, int boostBonusBp, int boostBids,AdventureDeckCards boostCardConditionCode, StoryDeckCards boostQuestConditionCode) {
        super(activeAbilityDescription, cardName, subType, imageFileName, cardCode);
        this.bonusBp = bonusBp;
        this.bids = bids;
        this.boostBonusBp = boostBonusBp;
        this.boostBids=boostBids;
        this.isBoosted=false;
        this.isCardBoosted=boostCardConditionCode!=null;
        this.isQuestBoosted=boostQuestConditionCode!=null;
        this.boostCardConditionCode = boostCardConditionCode;
        this.boostQuestConditionCode = boostQuestConditionCode;
    }

    @Override
    public void playCard() {

    }
}
