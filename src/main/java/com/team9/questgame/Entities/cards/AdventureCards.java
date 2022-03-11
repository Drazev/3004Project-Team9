package com.team9.questgame.Entities.cards;

import java.util.HashSet;

/**
 * Adventure cards represent cards that the player controls. Along with StoryCards, some Adventure cards
 * may be boosted by other Adventure Cards. Any adventure card can boost other cards and when this occurs
 * they will register to observe the card that boosted them. When that card is removed from a play area
 * for any reason they will be notified that their boost effect has ended. This works because in a given play area
 * no duplicates can be allowed.
 */
public abstract class AdventureCards extends Cards {

    private HashSet<BoostableCard> cardsBoostObservers;

    protected <T extends Enum<T> & AllCardCodes> AdventureCards(Decks assignedDeck,String activeAbilityDescription, String cardName, CardTypes subType, String fileName, T cardCode) {
        super(assignedDeck,activeAbilityDescription, cardName, subType, "./Assets/Adventure Deck (346x470)/"+fileName, cardCode);
        cardsBoostObservers = new HashSet<>();

    }

    @Override
    public String toString() {
        return super.toString()+", AdventureCards{}";
    }

    @Override
    public AllCardCodes getCardCode() {
        return super.getCardCode();
    }

    boolean registerBoostedCard(BoostableCard card) {
        LOG.debug("Registered card as observer to " + cardCode,card);
        return cardsBoostObservers.add(card);
    }

    boolean unregisterBoostCard(BoostableCard card) {
        LOG.debug("Un-Registered card as observer to " + cardCode,card);
        return cardsBoostObservers.remove(card);
    }

    /**
     * When a card is discarded or removed from a play area, inform
     * observers that boost has ended
     */
    @Override
    protected void onLocationChanged() {
        LOG.debug("AdventureCards::onLocationChanged triggered for "+cardCode);
        for(BoostableCard card : cardsBoostObservers) {
            card.notifyBoostEnded(location);
        }
        cardsBoostObservers.clear();
    }

    boolean playCard(PlayerPlayAreas playArea) {
        boolean rc=super.playCard(playArea);
        //Based on card attributes, register with PlayArea
        if(rc) {
            registerWithNewPlayArea(playArea);
        }
        return rc;
    }

    abstract protected void registerWithNewPlayArea(PlayerPlayAreas playArea);

}
