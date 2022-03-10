package com.team9.questgame.Entities.cards;

import com.team9.questgame.exception.IllegalCardStateException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class  Decks<T extends Cards> {
    private Logger LOG;
    protected HashSet<T> cardsInDeck;
    private Stack<T> drawDeck;
    @Getter
    private ArrayList<T> discardPile;
    private final HashMap<T, CardArea> cardLocation;
    protected static final CardFactory factory = CardFactory.getInstance();



    protected Decks(Class cType) {
        LOG=LoggerFactory.getLogger(cType);
        this.cardsInDeck=new HashSet<>();
        this.drawDeck = new Stack<>();
        this.discardPile = new ArrayList<>();
        this.cardLocation = new HashMap<>();
        init();
    }

    protected void init() {
        createDeck();
        shuffleDeck();
    }

    abstract protected void createDeck();

    /**
     * Deck is observer for cards and is notified
     * when a card is discarded so that it is added
     * to discard pile.
     * @param card
     */
    public void notifyDiscard(T card) {
        discardPile.add(card);
    }

    /**
     *Draws a card
     *
     * @param area The card area that where the CardArea::recieveCard() methiod will be triggered with given card.
     */
    public T drawCard(CardArea area) {
        T card=null;

        if(drawDeck.size()==0)
        {
            shuffleDeck();
        }

        if(drawDeck.size()<1) {
            LOG.error("Decks::drawCard has zero cards after shuffling in discard pile");
            throw new IllegalCardStateException();
        }

        card = drawDeck.pop();
        cardLocation.put(card,area);

        area.receiveCard(card);
        return card;
    }

    public void shuffleDeck() {
        ArrayList<T> shuffledDeck = new ArrayList<>();

        if(drawDeck.size()==0 && discardPile.size()==0) {
            shuffledDeck.addAll(cardsInDeck);
        }
        else {
            shuffledDeck.addAll(drawDeck);
            shuffledDeck.addAll(discardPile);
            drawDeck.clear();
        }

        Collections.shuffle(shuffledDeck);
        drawDeck.addAll(shuffledDeck);

        LOG.debug("Deck Shuffled.");
    }

    public void onGameReset() {
        drawDeck.clear();
        discardPile.clear();
        shuffleDeck();
        notifyDeckChanged();
    }

    abstract public void notifyDeckChanged();

    //TODO: Function that notifies observers or sends event to GameManager with new deck

}
