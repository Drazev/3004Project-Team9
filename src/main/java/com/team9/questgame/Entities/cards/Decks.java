package com.team9.questgame.Entities.cards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public abstract class  Decks<T extends Cards> {
    protected HashSet<T> cardsInDeck;
    private Stack<T> drawDeck;
    private ArrayList<T> discardPile;
    private HashMap<T,CardArea> cardLocation;
    protected static final CardFactory factory = CardFactory.getInstance();



    protected Decks() {
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

    public T drawCard(CardArea area) {
        if(drawDeck.size()==0)
        {
            shuffleDeck();
        }

        T card = drawDeck.pop();
        cardLocation.put(card,area);
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

        //TODO: Notify deck shuffled
    }

    //TODO: Function that notifies observers or sends event to GameManager with new deck

}
