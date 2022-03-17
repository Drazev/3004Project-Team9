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

    /**
     * Special case where a card is played into a Hand.
     * Activates a registration process specific to a
     * PlayerPlayArea card area.
     * @param playArea The PlayerPlayArea where the card now resides
     * @return True if the card was accepted into the hand, False otherwise
     */
    boolean playCard(PlayerPlayAreas playArea) {
        boolean rc=super.playCard(playArea);
        //Based on card attributes, register with PlayArea
        if(rc) {
            registerwithNewPlayArea(playArea);
            registerWithNewPlayerPlayArea(playArea);
        }
        return rc;
    }

    /**
     * General case when card is played into a play area.
     * Activates a registration process specific to
     * all play areas card area.
     * @param playArea The PlayerPlayArea where the card now resides
     * @return True if the card was accepted into the hand, False otherwise
     */
    boolean playCard(PlayAreas playArea) {
        boolean rc=super.playCard(playArea);
        //Based on card attributes, register with PlayArea
        if(rc) {
            registerwithNewPlayArea(playArea);
        }
        return rc;
    }

    /**
     * Special case where a card is played into a Hand.
     * Activates a registration process specific to a
     * Hand card area.
     * @param hand The hand where the card now resides
     * @return True if the card was accepted into the hand, False otherwise
     */
    boolean playCard(Hand hand) {
        boolean rc=super.playCard(hand);
        if(rc) {
            registerWithHand(hand);
        }
        return rc;
    }

    /**
     * If placed into a PlayerPlayArea then register to
     * notify PlayerPlayArea what attributes this card has that must be tracked.
     * This is optional for subclasses and must be overridden to
     * have an effect.
     * @param playArea The player play area where the card now resides
     */
    protected void registerWithNewPlayerPlayArea(PlayerPlayAreas playArea) {
        return;
    }

    /**
     * If placed into a PlayerPlayArea then register to
     * notify PlayerPlayArea what attributes this card has that must be tracked.
     * This is optional for subclasses and must be overridden to
     * have an effect.
     * @param playArea The player play area where the card now resides
     */
    protected void registerwithNewPlayArea(PlayAreas playArea) {
        return;
    }

    /**
     * If placed into hand, register any hand specific features.
     * This is optional for subclasses and must be overridden to
     * have an effect.
     * @param hand The hand where the card has been moved.
     */
    protected void registerWithHand(Hand hand) {
        return;
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

}
