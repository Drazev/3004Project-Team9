package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.DeckUpdateData;
import com.team9.questgame.Entities.DeckTypes;
import com.team9.questgame.exception.IllegalCardStateException;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

public abstract class  Decks<T extends Cards,U extends AllCardCodes> {
    final DeckTypes type;
    protected Logger LOG;
    protected HashSet<T> cardsInDeck;
    private Stack<T> drawDeck;
    @Getter
    private ArrayList<T> discardPile;
    protected static final CardFactory factory = CardFactory.getInstance();
    @JsonIgnore
    private final OutboundService outboundService;



    protected Decks(DeckTypes type, Class cType) {
        this.type = type;
        LOG=LoggerFactory.getLogger(cType);
        this.cardsInDeck=new HashSet<>();
        this.drawDeck = new Stack<>();
        this.discardPile = new ArrayList<>();
        this.outboundService = ApplicationContextHolder.getContext().getBean(OutboundService.class);
        init();
    }

    protected void init() {
        createDeck();
        shuffleDeck();
        notifyDeckChanged();
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
        T card=selectCard(area);
        card.playCard(area);
        return card;
    }

    protected T selectCard(CardArea area) {
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

        notifyDeckChanged();
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

    private DeckUpdateData generateDeckUpdateData() {
        ArrayList<CardData> data = new ArrayList<>();
        for(Cards card : discardPile) {
            data.add(card.generateCardData());
        }
        return new DeckUpdateData(
                type,
                drawDeck.size(),
                data
        );
    }

    public void testRebuildDeckWithList(HashMap<U,Integer> deckList) {
        HashSet<CardArea> ca = new HashSet<>();
        for(T card : cardsInDeck) {
            CardArea c = card.getLocation();
            if(c!=null) {
                ca.add(card.getLocation());
            }
            card.discardCard();
        }
        for(CardArea c : ca) {
            c.onGameReset();
        }
        cardsInDeck.clear();
        onGameReset();
    }

    protected void notifyDeckChanged() {
        outboundService.broadcastDeckUpdate(generateDeckUpdateData());
    }

    //TODO: Function that notifies observers or sends event to GameManager with new deck

}
