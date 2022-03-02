package com.team9.questgame.Entities.cards;

/**
 * Entity class representing an instance of a Quest Card type
 *
 * @param <T> A cardCode that represents an individual adventure card, or card gropuing that will be boosted by the quest
 *
 */
public class QuestCards<T extends Enum<T> & TargetableCardCodes> extends StoryCards {
    final int stages;
    final T boostedFoe;

    public QuestCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, StoryDeckCards cardCode, int stages) {
        this(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode,stages,null);
    }

    @Override
    public String toString() {
        return super.toString()+", QuestCards{" +
                "stages=" + stages +
                ", boostedFoe=" + boostedFoe +
                '}';
    }

    public QuestCards(Decks assignedDeck, String activeAbilityDescription, String cardName, CardTypes subType, String imgSrc, StoryDeckCards cardCode, int stages, T foeBoosted) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, imgSrc, cardCode);
        this.stages = stages;
        this.boostedFoe = foeBoosted;
    }

    public int getStages() {
        return stages;
    }

    public T getBoostedFoe() {
        return boostedFoe;
    }
}
