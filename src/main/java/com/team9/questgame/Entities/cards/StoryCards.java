package com.team9.questgame.Entities.cards;

import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.GamePhases.GamePhases;
import com.team9.questgame.QuestGameController;

import java.util.ArrayList;

public abstract class StoryCards extends Cards {

    protected <T extends Enum<T> & AllCardCodes> StoryCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, T cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, "./Assets/Story Deck (327x491)/"+fileName, cardCode);
    }

    @Override
    public String toString() {
        return super.toString()+", StoryCards{}";
    }

    abstract public GamePhases generateGamePhase(ArrayList<Players> players, QuestGameController gameInstance);

    @Override
    public CardData generateCardData() {
        CardData data = new CardData(
                cardID,
                cardCode,
                cardName,
                subType,
                imgSrc,
                0,
                0,
                activeAbilityDescription,
                false
        );
        return data;
    }

}
