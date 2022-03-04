package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.GamePhases.GamePhases;
import com.team9.questgame.QuestGameController;

import java.util.ArrayList;

public class TournamentCards extends StoryCards {
    final int bonusShields;

    @Override
    public String toString() {
        return super.toString()+", TournamentCards{" +
                "bonusShields=" + bonusShields +
                '}';
    }

    @Override
    public GamePhases generateGamePhase(ArrayList<Players> players, QuestGameController gameInstance) {
        return null;
    }


    public TournamentCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String fileName, StoryDeckCards cardCode, int bonusShields) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, fileName, cardCode);
        this.bonusShields = bonusShields;
    }
}
