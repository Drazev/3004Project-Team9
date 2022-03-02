package com.team9.questgame.Entities.cards;

public class TournamentCards extends StoryCards {
    final int bonusShields;

    @Override
    public String toString() {
        return super.toString()+", TournamentCards{" +
                "bonusShields=" + bonusShields +
                '}';
    }

    public TournamentCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, StoryDeckCards cardCode, int bonusShields) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
        this.bonusShields = bonusShields;
    }
}
