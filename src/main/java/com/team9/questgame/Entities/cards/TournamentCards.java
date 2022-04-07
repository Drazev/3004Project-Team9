package com.team9.questgame.Entities.cards;

import lombok.Getter;

public class TournamentCards extends StoryCards {
    @Getter
    final int bonusShields;

    @Override
    public String toString() {
        return super.toString()+", TournamentCards{" +
                "bonusShields=" + bonusShields +
                '}';
    }

    @Override
    protected void onLocationChanged() {

    }


    public TournamentCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, StoryDeckCards cardCode, int bonusShields) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.bonusShields = bonusShields;
    }
}
